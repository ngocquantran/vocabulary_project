$(function () {

  getCourseIdFromTopic(topicCourse);
  renderStudiedVocabs();

  learningCheckBtn1();
  learningViewportSlideBox();
  inputGuessWord();
  learningWords();
  updateLearningVocabProgress(
      $(".learning-word-layer").length,
      0,
      $(".learning-word-progress-range span"),
      $(".learning-word-progress-title strong")
  );
  playWordSound();

});


const URL_API = "http://localhost:8898/api/v1";
let params = new URLSearchParams(window.location.search);
let topicId = params.get("id");
let userId = "1";


//render back button

 function getCourseIdFromTopic(topicCourse) {
    $(".header-menu-item.menu-item-back a").attr(
      "href",
      `/course?id=${topicCourse.course.id}`
    );
    $(".header-menu-item.menu-item-back a span").text(
        topicCourse.title)
}

// Get Learning List Vocabs ------------------------------------------------------------------------------------------------


function createVocabTemplate(vocab) {
  const $template = $(
    document.querySelector(".vocab-template").content.firstElementChild
  ).clone();

  $template.attr({
    words: vocab.word,
    "words-id": vocab.id,
  });
  $template.find(".card-img img").attr("src", vocab.img);
  $template.find(".card-content-text").text(vocab.enMeaning);
  $template.find(".card-content-type").text(vocab.type);
  $template.find(".card-content-text-main").text(vocab.word);
  $template.find(".card-content-phonetic audio").attr("src", vocab.audio);
  $template.find(".card-content-phonetic span:last-child").text(vocab.phonetic);
  $template.find(".text-definition-vi").text(vocab.vnMeaning);
  $template.find(".card-content-example audio").attr("src", vocab.senAudio);
  $template.find(".example-vi").text(vocab.vnSentence);
// Tách câu ví dụ tiếng anh
  let enExample = vocab.enSentence.split("_");
  $template.find(".example-en span").eq(0).text(enExample[0]);
  $template.find(".example-en span").eq(1).text(enExample[1]);
  $template.find(".example-en span").eq(2).text(enExample[2]);


  return $template;
}

function createVocabList() {
  const list = studiedVocabs.map(function (vocab) {
    return createVocabTemplate(vocab);
  });
  return list;
}

function renderStudiedVocabs() {
  const $studiedContent = $(".learning-word-content");
  $studiedContent.html("");
  const list = createVocabList();
  $studiedContent.append(list);
}

// Learning page------------------------------------------------------------------------------------------------

function learningCheckBtn1() {
  const $inputArea = $(".learning-word-fill");

  $inputArea.each(function (index, area) {
    const $input = $(area).find("input");
    $input.on("keyup", function (e) {
      if (e.keyCode == 13 && $(this).val().length > 0) {
        $(this).blur();
        $(this).next("button").trigger("click");
      } else {
        $(area)
          .find("input.show-answer-right")
          .removeClass("show-answer-right");
        $(area)
          .find("input.show-answer-wrong")
          .removeClass("show-answer-wrong");
        if ($(this).val().length > 0) {
          $(this).next("button").addClass("btn-active");
        } else {
          $(this).next("button").removeClass("btn-active");
        }
      }
    });
  });
}


// Xoay flashcard từ vựng để học------------------------------------------------------------------
function learningViewportSlideBox() {
  const $cards = $(".learning-word-viewport-container");
  $cards.each(function (index, card) {
    const $btn = $(card).find(".card-turn");
    const $word = $(card).find(".learning-word-viewport-slide.slide2 audio");
    const $sentence = $(card).find(
      ".learning-word-viewport-slide.slide3 audio"
    );
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
      $(card).find(".learning-word-viewport-slide").removeClass("on");
      if (cur == 2) {
        playASound($word);
        $(card).find(".learning-word-viewport-slide.slide2").addClass("on");
      } else if (cur == 3) {
        playASound($sentence);
        $(card).find(".learning-word-viewport-slide.slide3").addClass("on");
      } else {
        $(card).find(".learning-word-viewport-slide.slide1").addClass("on");
      }
    });
  });
}

// Xoay đến mặt 2 sau khi gõ đáp án------------------------------------------------------------------

function showAnswer($currentCard) {
  const $viewPort = $currentCard.find(".learning-word-viewport-container");
  const $word = $currentCard.find(".learning-word-viewport-slide.slide2 audio");
  let curRotation = parseInt($viewPort.attr("rotate-data"));

  const $layer1 = $viewPort.find(".learning-word-viewport-slide.slide1.on");
  const $layer3 = $viewPort.find(".learning-word-viewport-slide.slide3.on");
  if ($layer1.length > 0) {
    curRotation -= 120;
    $viewPort.css({
      transform: `rotateX(${curRotation}deg)`,
    });
    $viewPort.attr("rotate-data", `${curRotation}`);
  } else if ($layer3.length > 0) {
    console.log("huhuhu");
    curRotation += 120;
    $viewPort.css({
      transform: `rotateX(${curRotation}deg)`,
    });
    $viewPort.attr("rotate-data", `${curRotation}`);
  }

  playASound($word);
}

// Nhập và check đáp án input------------------------------------------------------------------

function inputGuessWord() {
  const $learningLayer = $(".learning-word-layer");
  $learningLayer.each(function (index, layer) {
    let word = $(layer).attr("words");
    const $input = $(layer).find(".learning-word-fill input");
    const $checkBtn = $(layer).find(".learning-word-fill button");
    $checkBtn.on("click", function () {
      if ($input.val().length > 0) {
        if ($input.val().toLowerCase().trim() == word) {
          playSoundRight();
          $input.addClass("show-answer-right");
          showAnswer($(layer));
        } else {
          playSoundWrong();
          $input.addClass("show-answer-wrong");
        }
        $checkBtn.removeClass("btn-active");
      }
    });
  });
}


// Chuyển từ học tiếp/quay lại------------------------------------------------------------------

function disableLearningBtnForASecond() {
  const $btn = $(".learning-word-bottom-btn");
  $btn.css({ "pointer-events": "none" });
  setTimeout(() => {
    $btn.css({ "pointer-events": "all" });
  }, 1500);
}

function learningWords() {
  const $cards = $(".learning-word-layer");
  const n = $cards.length;
  $(".learning-word-progress-title span").text(`${n}`);

  let index = 0;
  setCurrentLearningCart($cards.eq(0));

  const $btnNext = $(".learning-word-bottom-btn.btn-next");
  $btnNext.on("click", function () {

    disableLearningBtnForASecond();
  
    $cards.each(function (index, card) {
      $(card).removeClass("left");
      $(card).addClass("right");
    });
    if (index < n - 1) {
      $(".learning-word-layer.hide").removeClass("hide");

      $cards.eq(index).addClass("hide");
      $cards.eq(index).removeClass("show");
      $cards.eq(index).removeClass("layer-current");
      index++;
      updateLearningVocabProgress(
        n,
        index,
        $(".learning-word-progress-range span"),
        $(".learning-word-progress-title strong")
      );

      setCurrentLearningCart($cards.eq(index));
    } else {
      window.location.href = `/test/vocab?id=${topicId}`;
    }
  });

  const $btnBack = $(".learning-word-bottom-btn.btn-back");
  $btnBack.on("click", function () {

     disableLearningBtnForASecond();
    $cards.each(function (index, card) {
      $(card).removeClass("right");
      $(card).addClass("left");
    });
    if (index > 0) {
      $(".learning-word-layer.hide").removeClass("hide");
      $cards.eq(index).removeClass("show");
      $cards.eq(index).removeClass("layer-current");
      $cards.eq(index).addClass("hide");
      index--;
      setCurrentLearningCart($cards.eq(index));
      updateLearningVocabProgress(
        n,
        index,
        $(".learning-word-progress-range span"),
        $(".learning-word-progress-title strong")
      );
    }
  });
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
    $sound[0].play();
  };
}

function playWordSound() {
  const $sound = $(".play-sound");
  $sound.each(function (index, sound) {
    const $btn = $(sound).find("i");
    const $mp3 = $(sound).find("audio");
    $btn.on("click", function () {
      playASound($mp3);
    });
  });
}
