$(function () {
  getCourseIdFromTopic(topicCourse);

  $(".test-intro .button-ready-content").on("click",  function () {
    renderQuestions2(vocabs);

    $(".test-intro").addClass("hidden");
    $(".test-container").removeClass("hidden");
    $(".test-exercise").eq(0).addClass("exercise-current");
    // countdown(15);
    playWordSound();
    runTest();
  });

});

// RENDER PAGE Test ---------------------------------------------------------------------------------------------------


let topicId = topicCourse.id;
let answerRequest = [];

//render back button

function getCourseIdFromTopic(topicCourse) {
  $(".header-menu-item.menu-item-back a").attr(
      "href",
      `/course?id=${topicCourse.course.id}`
  );
  $(".header-menu-item.menu-item-back a span").text(
      topicCourse.title)
}

// Có tối thiểu 12 loại câu hỏi, nếu số lượng nhiều hơn 12 sẽ load loại câu lại từ type 1

function renderQuestions2(arr) {
  const $testContent = $(".test-body-wrapper");
  $testContent.html("");
  let html = "";
  arr.forEach((question, no) => {
    let type = no < 12 ? no + 1 : no - 12 + 1;
    // Tách từ trong câu tiếng anh để render
    let enSentence = question.enSentence.split("_");
    html +=
      ` <div
                  class="test-exercise"
                  index="${no + 1}"
                  typequestion="${type}"
                  typeanswer="${type <= 7 ? "choose" : "write"}"
                  answer="${question.word}"
                  answer-index="${question.answerIndex}"
                  word-id="${question.id}"
                >
                  <div class="test-exercise-question">
                    <div class="test-exercise-question-viewport">
                      <div class="test-exercise-question-view-front">
                        <div class="test-exercise-content">
                          <p class="test-exercise-title">
                            ${
                              type == 1 || type == 3
                                ? "Chọn từ phù hợp theo các gợi ý sau:"
                                : type == 2
                                ? "Chọn nghĩa đúng với từ vựng sau:"
                                : type == 4
                                ? "Chọn từ đúng với định nghĩa sau:"
                                : type == 5
                                ? "Chọn từ đúng với âm thanh sau:"
                                : type == 6
                                ? "Chọn từ đúng điền vào câu sau:"
                                : type == 7
                                ? "Chọn câu tiếng anh ứng với nội dung sau:"
                                : type == 8
                                ? "Gõ từ đúng với phiên âm sau:"
                                : type == 9 || type == 10
                                ? "Gõ từ phù hợp theo các gợi ý sau:"
                                : "Gõ từ đúng với định nghĩa sau:"
                            }
                          </p>
                        </div>` +
      (type == 1
        ? `<div class="test-exercise-question">
                          <div class="play-sound">
                            <i class="fa-solid fa-volume-high"></i>
                            <audio
                              src="${question.audio}"
                            ></audio>
                          </div>
                          <p class="test-exercise-question-phonetic">${question.phonetic}</p>
                        </div>`
        : type == 2
        ? `<div class="test-exercise-question">
                          <p class="test-exercise-question-word">
                            ${question.word} <span>${question.phonetic}</span>
                          </p>
                        </div>`
        : type == 3
        ? `<div class="test-exercise-question">
                          <div class="question-hint">
                            <img
                              class="hint-image"
                              src="${question.img}"
                              alt="img"
                            />
                            <p class="hint-word">
                              ${question.vnMeaning}
                              <span>${question.type}</span>
                            </p>
                          </div>
                        </div>`
        : type == 4
        ? `<div class="test-exercise-question">
                          <p class="question-definition">
                            ${question.enMeaning}
                          </p>
                          <span class="question-word-type">${question.type}</span>
                        </div>`
        : type == 5
        ? `<div class="test-exercise-question">
                          <div class="test-exercise-question-sound play-sound">
                            <i class="fa-solid fa-volume-high"></i>
                            <audio
                              src="${question.audio}"
                            ></audio>
                          </div>
                        </div>`
        : type == 6
        ? `<div class="test-exercise-question">
                          <p class="question-missing-sentence">
                            <span>${enSentence[0]}</span>
                            <span>_ _ _ _ _</span>
                            <span>${enSentence[2]}</span>
                          </p>
                        </div>`
        : type == 7
        ? `<div class="test-exercise-question">
                          <p class="question-vi-sentence">
                            ${question.vnSentence}
                          </p>
                        </div>`
        : type == 8
        ? `<div class="test-exercise-question">
                          <div class="test-exercise-question-sound play-sound">
                            <i class="fa-solid fa-volume-high"></i>
                            <audio
                              src="${question.audio}"
                            ></audio>
                          </div>
                          <p class="question-phonetic">${question.phonetic}</p>
                        </div>`
        : type == 9 || type == 10
        ? `<div class="test-exercise-question">
                          <div class="question-hint">
                            <img
                              class="hint-image"
                              src="${question.img}"
                              alt="img"
                            />
                            <p class="hint-word">
                             ${question.vnMeaning}
                              <span>${question.type}</span>
                            </p>
                          </div>
                        </div>`
        : `<div class="test-exercise-question">
                          <p class="question-definition">
                            ${question.enMeaning}
                          </p>
                          <span class="question-word-type">${question.type}</span>
                        </div>`) +
      `
                        
                      </div>

                      <div class="test-exercise-question-view-back">` +
      (type < 6 || type > 7
        ? `<div class="test-exercise-question-view-back-img">
                            <img
                              src="${question.img}"
                              alt=""
                            />
                          </div>

                          <div class="test-exercise-question-view-back-content">
                            <p class="word-content">
                              ${question.word} <span class="word-content-type">${question.type}</span>
                            </p>

                            <p class="word-content-phonetic">
                              <span class="play-sound">
                                <i class="fa-solid fa-volume-high"></i>
                                <audio
                                  src="${question.audio}"
                                ></audio>
                              </span>
                              <span class="word-content-phonetic-result"
                                >${question.phonetic}</span
                              >
                            </p>

                            <div class="text-definition-vi">${question.vnMeaning}</div>
                          </div>`
        : `<div class="testing-exercise-result-cover">
                          <div class="play-sound">
                            <i class="fa-solid fa-volume-high"></i>
                            <audio
                              src="${question.senAudio}"
                            ></audio>
                          </div>
                          <p class="result-content-example">
                            <span>${enSentence[0]}</span><span>${enSentence[1]}</span><span>${enSentence[2]}</span>
                          </p>
                          <p class="result-content-example-translate">
                            ${question.vnSentence}
                          </p>
                        </div>`) +
      `</div>
                    </div>
                  </div>` +
      (type <= 7
        ? `

                  <div class="test-exercise-answer">
                    <div class="answer-wrapper">
                      <div class="answer-choice" value="1">
                        <span>${
                          type == 1 || type == 3 || type == 4 || type == 6
                            ? question.vocabs[0]
                            : type == 2 || type == 5
                            ? question.vnMeanings[0]
                            : question.enSentences[0].replaceAll("_", "")
                        }</span>
                      </div>
                      <div class="answer-choice" value="2">
                        <span>${
                          type == 1 || type == 3 || type == 4 || type == 6
                            ? question.vocabs[1]
                            : type == 2 || type == 5
                            ? question.vnMeanings[1]
                            : question.enSentences[1].replaceAll("_", "")
                        }</span>
                      </div>
                      <div class="answer-choice" value="3">
                        <span>${
                          type == 1 || type == 3 || type == 4 || type == 6
                            ? question.vocabs[2]
                            : type == 2 || type == 5
                            ? question.vnMeanings[2]
                            : question.enSentences[2].replaceAll("_", "")
                        }</span>
                      </div>
                      <div class="answer-choice" value="4">
                        <span>${
                          type == 1 || type == 3 || type == 4 || type == 6
                            ? question.vocabs[3]
                            : type == 2 || type == 5
                            ? question.vnMeanings[3]
                            : question.enSentences[3].replaceAll("_", "")
                        }</span>
                      </div>
                    </div>
                  </div>
                </div>`
        : `<div class="test-exercise-answer answer-write">
                    <div class="answer-cover">
                      <input
                        class="answer-writing"
                        placeholder="Gõ đáp án của bạn tại đây"
                        name=""
                      />
                      <div class="answer-check">Kiểm tra</div>
                    </div>
                  </div>
                </div>`);
  });
  $testContent.append(html);
}

// Countdown Timer-------------------------------------------------------------------------------------------------
let timeCount;

function countdown(time) {
  const $time = $(".test-countdown-number");
  $time.text(`${time}`);
  timeCount = setInterval(function () {
    time--;
    if (time < 10) {
      $time.text("0" + `${time}`);
      playClockSound();
    } else {
      $time.text(`${time}`);
      playClockSound();
    }

    if (time == 0) {
      collectAnswerResult(false);
      timeOut();

      // clearInterval(timeCount);
    }
  }, 1000);
}

async function timeOut() {
  await stopCountDown();
  await disableChoosing();
  playSoundWrong();
  await showAnswer();
  await nextQuestion();
}

function stopCountDown() {
  window.clearInterval(timeCount);
}

// Run test ---------------------------------------------------------------------------------------------------------------------------

function updateTestProgress() {
  const $exercise = $(".test-exercise");
  const n = $exercise.length;
  $(".progress-title span").text(n);
  const $cur = $(".test-exercise.exercise-current");
  let index = parseInt($cur.attr("index"));
  $(".progress-title strong").text(index);
  $(".test-progress-bar").css({
    width: (index / n) * 100 + "%",
  });
}

function showAnswer() {
  const $cur = $(".test-exercise.exercise-current");
  const $view = $cur.find(".test-exercise-question-viewport");
  const $audio = $cur.find(".test-exercise-question-view-back audio");
  setTimeout(function () {
    $cur.addClass("finish");
    $view.addClass("show-answer");
    playASound($audio);
  }, 500);
}

function runTest() {
  getReadyToRun($(".test-exercise.exercise-current"));
  countdown(15);
  updateTestProgress();
  checkAnswerGuessing();
  activeSubmit();
  checkAnswerWriting();
}

function nextQuestion() {
  const $cur = $(".test-exercise.exercise-current");
  const $next = $cur.next();

  if (!$next.length == 0) {
    setTimeout(function () {
      $cur.removeClass("exercise-current");
      $next.addClass("exercise-current");
      runTest();
    }, 3000);
  } else {
    setTimeout(async () => {
      try {

        postTestResult(answerRequest);
      } catch (error) {
        console.log(error);
      }
    }, 3000);
  }
}

// Gửi danh sách kết quả kiểm tra từ vựng
async function postTestResult(obj) {
  try {

    $("#loadMe").modal("show");
    let res = await axios.post(
      `/api/vocab-test-result?id=${topicId}`,
      obj
    );
    window.location.href = `/result?id=${topicId}`;
  } catch (error) {
    console.log(error);
  }
}

function getReadyToRun($currentExercise) {
  const $input = $currentExercise.find(
    ".test-exercise-answer.answer-write input"
  );
  $input.focus();
  const $audio = $currentExercise.find(
    ".test-exercise-question-view-front audio"
  );
  if ($audio.length > 0) {
    playASound($audio);
  }
}

// Check answer-------------------------------------------------------------------------------------------------------

function checkAnswerGuessing() {
  const $cur = $(".test-exercise.exercise-current");
  const answerIndex = parseInt($cur.attr("answer-index"));
  const $allChoice = $cur.find(".answer-choice");
  const $rightChoice = $cur.find(`.answer-choice[value=${answerIndex}]`);
  const $wrongChoice = $cur.find(`.answer-choice:not([value=${answerIndex}])`);

  $rightChoice.on("click", function () {
    stopCountDown();
    collectAnswerResult(true);
    $rightChoice.addClass("choose-right");
    playSoundRight();

    showAnswer();
    disableChoosing();
    nextQuestion();
  });

  $wrongChoice.each(function (index, choice) {
    $(choice).on("click", function () {
      stopCountDown();
      collectAnswerResult(false);

      $rightChoice.addClass("choose-right");
      $(choice).addClass("choose-wrong");
      playSoundWrong();
      showAnswer();
      disableChoosing();
      nextQuestion();
    });
  });
}

function disableChoosing() {
  const $allChoice = $(".test-exercise.exercise-current .answer-choice");
  $allChoice.each(function (index, option) {
    $(option).css({
      "pointer-events": "none",
    });
  });
}

function checkAnswerWriting() {
  const $cur = $(".test-exercise.exercise-current");
  const answer = $cur.attr("answer");
  const $input = $cur.find(".answer-writing");
  const $btn = $cur.find(".answer-check");
  $btn.on("click", function () {
    if ($input.val().toLowerCase().trim() == answer) {
      playSoundRight();
      $input.addClass("show-answer-right");
      collectAnswerResult(true);
    } else {
      playSoundWrong();
      $input.addClass("show-answer-wrong");
      collectAnswerResult(false);
    }
    stopCountDown();
    showAnswer();
    nextQuestion();
  });
}

function collectAnswerResult(status) {
  let id = $(".test-exercise.exercise-current").attr("word-id");
  let time = 15 - parseInt($(".test-countdown-number").text());
  answerRequest.push({
    vocabId: id,
    status: status,
    testTime: time,
  });
  console.log(answerRequest);
}

// active submit button when input--------------------------------------------------------------------------------

function activeSubmit() {
  const $answer = $(".test-exercise.exercise-current .test-exercise-answer");
  const $input = $answer.find(".answer-writing");
  const $btn = $answer.find(".answer-check");
  $input.on("keyup", function (e) {
    if (e.keyCode == 13 && $input.val().length > 0) {
      $input.blur();
      $btn.trigger("click");
      $btn.css({
        "pointer-events": "none",
      });
      $btn.removeClass("active-btn");
    } else {
      if ($input.val().length > 0) {
        $btn.addClass("active-btn");
      } else {
        $btn.removeClass("active-btn");
      }
    }
  });
}

// Audio Play---------------------------------------------------------------------------------------------

function playASound($sound) {
  $sound[0].load();
  $sound[0].onloadeddata = function () {
    $sound[0].play();
  };
}

function playClockSound() {
  $(".clock-sound")[0].load();
  $(".clock-sound")[0].onloadeddata = function () {
    $(".clock-sound")[0].play();
  };
}

function playSoundRight() {
  $(".answer-right-sound")[0].load();
  $(".answer-right-sound")[0].onloadeddata = function () {
    $(".answer-right-sound")[0].play();
  };
}

function playSoundWrong() {
  $(".answer-wrong-sound")[0].load();
  $(".answer-wrong-sound")[0].onloadeddata = function () {
    $(".answer-wrong-sound")[0].play();
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
