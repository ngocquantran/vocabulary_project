$(function () {
  renderSenTemplate(sentence);
  renderContextModalWindow();

  learningViewportSlideBox();
  playSentenceByWords();

  playWordSound();
  $(".context-content .context-title audio")[0].ontimeupdate = function () {
    showContextPlaying(this);
  };
  playEachContext();
});

let contextsBySentence = [];

// Tạo template và render nội dung học

function renderSenTemplate(sen) {
  const $template = $(".learning-sen-content");

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



// Render nội dung cửa sổ xem context
function renderContextModalWindow() {
  const $container = $("#context-modal");

  $container
    .find(".context-title audio")
    .attr("src", sentence.contextAudio);
  $container.find(".context-title h5").text(sentence.enContextDesc);
  $container.find(".context-title h6").text(sentence.vnContextDesc);

  $container.find(".context-content-item").html("");
  let html = "";
  contexts.forEach((context) => {
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

//MP3-------------------------------------------------------------------------------------------------------------------------------

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
