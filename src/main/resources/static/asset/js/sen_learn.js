$(function () {
  getCourseIdFromTopic(topicCourse);
  renderStudiedSen();
  openContextModalWindow();

  learningViewportSlideBox();
  playSentenceByWords();

  learningWords();
  updateLearningVocabProgress(
      $(".learning-sen-layer").length,
      0,
      $(".learning-sen-progress-range span"),
      $(".learning-sen-progress-title strong")
  );
  playWordSound();

});



let contextsBySentence = [];


//render back button

function getCourseIdFromTopic(topicCourse) {
  $(".header-menu-item.menu-item-back a").attr(
      "href",
      `/course?id=${topicCourse.course.id}`
  );
  $(".header-menu-item.menu-item-back a span").text(
      topicCourse.title)
}


// Tạo template và render nội dung học

function createSenTemplate(sen) {
  const $template = $(
    document.querySelector(".sen-template").content.firstElementChild
  ).clone();

  $template.attr({
    "sen-id": sen.id,
  });
  $template.find(".card-img img").attr("src", sen.img);
  $template.find(".sentence .content").text(sen.content.replaceAll("_", " "));
  $template
    .find(".sentence .pronounce")
    .text(sen.phonetic.replaceAll("_", " "));
  let renderWord = separateSentenceData(
    sen.content,
    sen.phonetic,
    sen.wordsTimestamp
  );
  renderWord.forEach((data) => {
    $template
      .find(".word .content")
      .append(
        ` <span data-start="${data.data_start}" data-end="${data.data_end}" class="">${data.word}</span>`
      );
    $template
      .find(".word .pronounce")
      .append(
        ` <span data-start="${data.data_start}" data-end="${data.data_end}" class="">${data.pronounce}</span>`
      );
  });

  $template.find(".sound .sound-sentence audio").attr("src", sen.senAudio);
  $template.find(".sound .sound-word audio").attr("src", sen.wordsAudio);
  $template.find(".sentence-vi .content").text(sen.vnSentence);
  $template.find(".usage .content p").text(sen.apply);
  $template.find(".btn-context").attr("id-sentence", sen.id);
  $template.find(".card-content-example audio").attr("src", sen.senAudio);
  $template
    .find(".example-group .example-en")
    .text(sen.content.replaceAll("_", " "));
  $template.find(".example-group .example-vi").text(sen.vnSentence);

  return $template;
}

function separateSentenceData(content, phonetic, timestamp) {
  let contents = content.split("_");
  let phonetics = phonetic.split("_");
  let stamps = timestamp.split("_");
  let start = 0;
  let renderArr = [];
  for (let i = 0; i < stamps.length; i++) {
    renderArr.push({
      data_start: start,
      data_end: stamps[i],
      word: contents[i],
      pronounce: phonetics[i],
    });
    start = stamps[i];
  }
  return renderArr;
}

function createSenList() {
  const list = studiedSen.map(function (sen) {
    return createSenTemplate(sen);
  });
  return list;
}

function renderStudiedSen() {
  const $studiedContent = $(".learning-sen-content");
  $studiedContent.html("");
  const list = createSenList();
  $studiedContent.append(list);
}

// Get Context content by sentence Id
function openContextModalWindow() {
  const $contextBtn = $(".btn-context");
  $contextBtn.each(function (index, btn) {
    $(btn).on("click", function () {
      let senId = $(this).attr("id-sentence");
      getContextContent(senId);
    });
  });
}

// API lấy nội dung context theo Sentence

async function getContextContent(senId) {
  try {
    let res = await axios.get(
      `/api/sentence/${senId}/context`
    );
    contextsBySentence = res.data;
  
    console.log(contextsBySentence);
    renderContextModalWindow(senId);
    $(".context-content .context-title audio")[0].ontimeupdate = function () {
      showContextPlaying(this);
    };
    playEachContext();
  } catch (error) {
    console.log(error);
  }
}



// Render nội dung cửa sổ xem context
function renderContextModalWindow(senId) {
  const $container = $("#context-modal");
  let chosedSen = studiedSen.filter((sen) => {
    return sen.id == senId;
  });
  console.log(chosedSen);
  $container
    .find(".context-title audio")
    .attr("src", chosedSen[0].contextAudio);
  $container.find(".context-title h5").text(chosedSen[0].enContextDesc);
  $container.find(".context-title h6").text(chosedSen[0].vnContextDesc);

  $container.find(".context-content-item").html("");
  let html = "";
  contextsBySentence.forEach((context) => {
    html += ` <div
                      class="context-item"
                      data-start="${context.startTime}"
                      data-end="${context.endTime}"
                    >
                      <div class="context-image">
                        <h4>${context.name}</h4>
                        <img
                          class="speaker"
                          src="${context.img}.jpg"
                        />
                        <img
                          class="speaker-active hidden"
                          src="${context.img}.gif"
                        />
                      </div>
                      <div class="context-text">
                        <h5>
                         ${context.enSentence.replaceAll("_", "")}
                        </h5>
                        <h6>
                          ${context.vnSentence}
                        </h6>
                        <i class="first"></i>
                      </div>
                    </div>`;
  });
  $container.find(".context-content-item").append(html);
}

// Xoay flashcard từ vựng để học------------------------------------------------------------------
function learningViewportSlideBox() {
  const $cards = $(".learning-sen-viewport-container");
  $cards.each(function (index, card) {
    const $btn = $(card).find(".card-turn");
    const $word = $(card).find(".learning-sen-viewport-slide.slide2 audio");
    const $sentence = $(card).find(".learning-sen-viewport-slide.slide3 audio");
    // let pos = 0;
    let pos = parseInt($(card).attr("rotate-data"));
    let cur = 1;
    $btn.on("click", function () {
      pos -= 120;
      $(card).attr("rotate-data", `${pos}`);
      cur++;
      if (cur > 3) {
        cur = 1;
      }
      $(card).css({
        transform: `rotateX(${pos}deg)`,
      });
      $(card).find(".learning-sen-viewport-slide").removeClass("on");
      if (cur == 2) {
        // playASound($word);
        $(card).find(".learning-sen-viewport-slide.slide2").addClass("on");
      } else if (cur == 3) {
        // playASound($sentence);
        $(card).find(".learning-sen-viewport-slide.slide3").addClass("on");
      } else {
        $(card).find(".learning-sen-viewport-slide.slide1").addClass("on");
      }
    });
  });
}

// Chuyển từ học tiếp/quay lại------------------------------------------------------------------

function learningWords() {
  const $cards = $(".learning-sen-layer");
  const n = $cards.length;
  $(".learning-sen-progress-title span").text(`${n}`);

  let index = 0;
  setCurrentLearningCart($cards.eq(0));

  const $btnNext = $(".learning-sen-bottom-btn.btn-next");
  $btnNext.on("click", function () {
    disableLearningBtnForASecond();

    $cards.each(function (index, card) {
      $(card).removeClass("left");
      $(card).addClass("right");
    });
    if (index < n - 1) {
      $(".learning-sen-layer.hide").removeClass("hide");

      $cards.eq(index).addClass("hide");
      $cards.eq(index).removeClass("show");
      $cards.eq(index).removeClass("layer-current");
      index++;
      updateLearningVocabProgress(
        n,
        index,
        $(".learning-sen-progress-range span"),
        $(".learning-sen-progress-title strong")
      );

      setCurrentLearningCart($cards.eq(index));
    } else {
      window.location.href = `/test/sentence?id=${topicId}`;
    }
  });

  const $btnBack = $(".learning-sen-bottom-btn.btn-back");
  $btnBack.on("click", function () {
    disableLearningBtnForASecond();
    
    $cards.each(function (index, card) {
      $(card).removeClass("right");
      $(card).addClass("left");
    });
    if (index > 0) {
      $(".learning-sen-layer.hide").removeClass("hide");
      $cards.eq(index).removeClass("show");
      $cards.eq(index).removeClass("layer-current");
      $cards.eq(index).addClass("hide");
      index--;
      setCurrentLearningCart($cards.eq(index));
      updateLearningVocabProgress(
        n,
        index,
        $(".learning-sen-progress-range span"),
        $(".learning-sen-progress-title strong")
      );
    }
  });
}

function disableLearningBtnForASecond() {
  const $btn = $(".learning-sen-bottom-btn");
  $btn.css({ "pointer-events": "none" });
  setTimeout(() => {
    $btn.css({ "pointer-events": "all" });
  }, 1500);
}

function setCurrentLearningCart($card) {
  $card.addClass("show");
  $card.addClass("layer-current");
  // $card.find(".filter-word-item-sound audio").get(0).play();
}

function updateLearningVocabProgress(
  totalLength,
  curIndex,
  $progressValue,
  $progressValueText
) {
  $progressValue.css({
    width: ((curIndex + 1) / totalLength) * 100 + "%",
  });
  $progressValueText.text(curIndex + 1);
}

//MP3-------------------------------------------------------------------------------------------------------------------------------

function playSoundRight() {
  $(".sound-answer-right")[0].load();
  $(".sound-answer-right")[0].onloadeddata = function () {
    $(".sound-answer-right")[0].play();
  };
}

function playSoundWrong() {
  $(".sound-answer-wrong")[0].load();
  $(".sound-answer-wrong")[0].onloadeddata = function () {
    $(".sound-answer-wrong")[0].play();
  };
}

function playASound($sound) {
  $sound[0].load();
  $sound[0].onloadeddata = function () {
    pauseEverySound();
    $sound[0].play();
  };
}

function pauseEverySound() {
  $("audio").each(function (index, audio) {
    $(audio)[0].pause();
  });
}

function playWordSound() {
  const $sound = $(".play-sound");
  $sound.each(function (index, sound) {
    const $btn = $(sound).find("i");
    const $mp3 = $(sound).find("audio");
    $btn.on("click", function () {
      pauseEverySound();
      playASound($mp3);
    });
  });
}

function playASoundInTime($sound, start, end) {
  // return new Promise(function (resolve, reject) {
  $sound[0].load();
  let duration = $sound[0].duration;
  console.log(duration);
  if (start > duration || end > duration || start > end) {
    console.log("Kiểm tra lại timestamp");
    return;
  }
  $sound[0].onloadeddata = function () {
    $sound[0].currentTime = start / 1000;
    pauseEverySound();
    $sound[0].play();
    setTimeout(() => {
      $sound[0].pause();
      $sound[0].currentTime = 0;
      // resolve();
    }, end - start);
  };
  // });
}

// function playEachContext2() {
//   const $contextContent = $(".context-group-content");
//   const $sound = $contextContent.find(".context-title .play-sound audio");
//   const $contexts = $(".context-item");

//   $contexts.each(function (index, context) {
//     $(context).on("click", async function () {
//       $(context).toggleClass("context-active");
//       $(context).find(".speaker, .speaker-active").toggleClass("hidden");

//       playASoundInTime(
//         $sound,
//         $(context).attr("data-start"),
//         $(context).attr("data-end")
//       ).then(() => {
//         $(context).toggleClass("context-active");
//         $(context).find(".speaker, .speaker-active").toggleClass("hidden");
//       });
//     });
//   });
// }

// Play context và sentence theo timestamp

function playEachContext() {
  const $contextContent = $(".context-group-content");
  const $sound = $contextContent.find(".context-title .play-sound audio");
  const $contexts = $(".context-item");

  $contexts.each(function (index, context) {
    $(context).on("click", function () {
      playASoundInTime(
        $sound,
        $(context).attr("data-start"),
        $(context).attr("data-end")
      );
    });
  });
}

function showContextPlaying(sound) {
  const $contexts = $(".context-content .context-item");
  // const sound = $(".context-content .context-title audio")[0];
  const timestamp = [];
  $contexts.each(function (index, context) {
    timestamp.push({
      start: parseInt($(context).attr("data-start")),
      end: parseInt($(context).attr("data-end")),
    });
  });

  timestamp.forEach(function (time, index) {
    if (
      sound.currentTime * 1000 < time.end &&
      sound.currentTime * 1000 > time.start
    ) {
      $contexts.eq(index).addClass("context-active");
      $contexts.eq(index).find(".speaker").addClass("hidden");
      $contexts.eq(index).find(".speaker-active").removeClass("hidden");
    } else {
      $contexts.eq(index).removeClass("context-active");
      $contexts.eq(index).find(".speaker").removeClass("hidden");
      $contexts.eq(index).find(".speaker-active").addClass("hidden");
    }
  });
}

function playSentenceByWords() {
  const $layer = $(".learning-sen-layer.layer");
  $layer.each(function (index, layer) {
    const sound = $(layer).find(".sound .sound-word audio")[0];
    const $words = $(layer).find(".word .content span");

    const timestamp = [];
    $words.each(function (index, word) {
      timestamp.push({
        start: parseInt($(word).attr("data-start")),
        end: parseInt($(word).attr("data-end")),
      });
    });

    const $phonetics = $(layer).find(".word .pronounce span");
    sound.ontimeupdate = function () {
      timestamp.forEach(function (time, index) {
        if (
          sound.currentTime * 1000 < time.end &&
          sound.currentTime * 1000 > time.start
        ) {
          $words.eq(index).addClass("color-active");
          $phonetics.eq(index).addClass("color-active");
        } else {
          $words.eq(index).removeClass("color-active");
          $phonetics.eq(index).removeClass("color-active");
        }
      });
    };
  });
}
