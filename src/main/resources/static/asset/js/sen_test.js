$(function () {
    getCourseIdFromTopic(topicCourse);
    getTestContent();
    $(".test-intro .button-ready-content").on("click", async function () {
        await renderContextQuestion();
        $(".test-intro").addClass("hidden");
        $(".test-container").removeClass("hidden");
        $(".test-exercise").eq(0).addClass("exercise-current");
        playWordSound();
        // countdown(15);
        runTest();
    });

});

// Get Test data---------------------------------------------------------------------------------------------------


let answerRequest = [];


function getCourseIdFromTopic(topicCourse) {
    $(".header-menu-item.menu-item-back a").attr(
        "href",
        `/course?id=${topicCourse.course.id}`
    );
    $(".header-menu-item.menu-item-back a span").text(
        topicCourse.title)
}

function getTestContent() {
    let list = createQuestionList();
    console.log(list);
    renderQuestionList(list);

}

// Tạo list câu hỏi theo option 8 loại câu hỏi, trong đó mỗi câu sẽ được render 3 câu hỏi, shuffle thứ tự câu trước khi trả về dãy
function createQuestionList() {
    let list = [];
    sens.forEach((sen) => {
        list.push(sen);
        list.push(sen);
        list.push(sen);
    });
    let listQuestions = [];
    let questionType = 1;
    while (listQuestions.length < 15) {
        if (questionType > 8) {
            questionType -= 8;
        }
        let rd = Math.floor(Math.random() * list.length);

        if (questionType == 1 || questionType == 4) {
            listQuestions.push({
                type: questionType,
                sen: list[rd],
                answer: "write",
            });
        } else if (
            questionType == 2 ||
            questionType == 5 ||
            questionType == 6 ||
            questionType == 7 ||
            questionType == 8
        ) {
            listQuestions.push({
                type: questionType,
                sen: list[rd],
                answer: "choose",
            });
        } else if (questionType == 3) {
            listQuestions.push({
                type: questionType,
                sen: list[rd],
                answer: "shuffle",
            });
        }
        list.splice(rd, 1);
        questionType++;
    }
    return shuffleArray(listQuestions);
}

function shuffleArray(a) {
    var j, x, i;
    for (i = a.length - 1; i > 0; i--) {
        j = Math.floor(Math.random() * (i + 1));
        x = a[i];
        a[i] = a[j];
        a[j] = x;
    }
    return a;
}

function renderQuestionList(qArr) {
    const $container = $(".test-body-wrapper");
    $container.html("");
    let html = "";
    qArr.forEach((question) => {
        html += renderQuestion(question.sen, question.type, question.answer);
    });
    $container.append(html);
}

// Render từng câu hỏi một dựa theo câu, loại câu hỏi, loại đáp án
function renderQuestion(sen, qType, aType) {
    let answerIndex = 0;
    let answerChoices = [];
    let shuffleArr = [];
    let htmlShuffle = "";

    if (aType == "choose") {
        answerIndex = Math.floor(Math.random() * 4) + 1;
        switch (qType) {
            case 2:
                answerChoices = createAnswerChoice(answerIndex, sen, "content");
                break;
            case 5:
                answerChoices = createAnswerChoice(answerIndex, sen, "vnSentence");
                break;
            case 6:
                answerChoices = createAnswerChoice(answerIndex, sen, "content");
                break;
            case 7:
                answerChoices = createAnswerChoice(answerIndex, sen, "vnSentence");
                break;
            case 8:
                answerChoices = createAnswerChoice(answerIndex, sen, "content");
                break;
        }
    }

    if (aType == "shuffle") {
        shuffleArr = sen.content.split("_");
        while (shuffleArr.length > 0) {
            let rd = Math.floor(Math.random() * shuffleArr.length);
            htmlShuffle += ` <li class="ui-state-default">${shuffleArr[rd]}</li>`;
            shuffleArr.splice(rd, 1);
        }
        // shuffleArr.forEach((word) => {
        //   htmlShuffle += ` <li class="ui-state-default">${word}</li>`;
        // });
    }

    let html =
        `<div class="test-exercise"
                  typequestion="${qType}"
                  typeanswer="${aType}"
                    id-sen="${sen.id}"
                >
                  <div class="test-exercise-question-viewport">
                    <div class="test-exercise-content">
                      <p class="question-title">${
            qType == 1
                ? "Viết hoàn chỉnh câu văn theo phiên âm sau:"
                : qType == 2
                ? "Chọn câu văn đúng theo gợi ý sau:"
                : qType == 3
                    ? "Sắp xếp những từ sau thành câu hoàn chỉnh theo gợi ý:"
                    : qType == 4
                        ? "Nghe và viết lại câu văn hoàn chỉnh với gợi ý:"
                        : qType == 5
                            ? "Nghe và chọn nghĩa đúng của câu:"
                            : qType == 6
                                ? "Chọn câu văn phù hợp với cách dùng sau:"
                                : qType == 7
                                    ? "Chọn ý nghĩa đúng của câu văn sau:"
                                    : qType == 8
                                        ? "Chọn câu văn còn thiếu trong ngữ cảnh sau:"
                                        : ""
        }</p>
                      <p class="question-guide">
                      ${
            qType == 1
                ? "(Lưu ý quy tắc viết hoa, chính tả và dấu câu!)"
                : qType == 3
                ? "(Di chuyển các từ/cụm từ sau theo đúng vị trí để tạo thành câu hoàn chỉnh)"
                : qType == 4
                    ? " (Lưu ý quy tắc viết hoa, chính tả và dấu câu!)"
                    : qType == 8
                        ? sen.enContextDesc
                        : ""
        }
                        
                      </p>
                    </div>
                    <div class="test-exercise-question">
                      <div class="test-exercise-question-sound play-sound">
                        <!-- <i class="fa-solid fa-volume-high"></i> -->
                        <audio
                          src="${sen.senAudio}"
                        ></audio>
                      </div>

                      <div class="test-exercise-question-content">` +
        (qType == 8
            ? ` <div class="context-list">
                         `
            : `
                        <div class="content-left">
                          <img
                            src="/asset/img/common/person-${qType}.png"
                            alt=""
                            class="img-person"
                          />
                          <img
                            src="/asset/img/common/person-${qType}.gif"
                            alt=""
                            class="img-success hidden"
                          />
                          <img
                            src="/asset/img/common/fail-${qType}.gif"
                            alt=""
                            class="img-fail hidden"
                          />
                        </div>

                        <div class="content-right">
                          <span class="content-right-question"
                            >${
                qType == 1
                    ? sen.phonetic.replaceAll("_", " ")
                    : qType == 2 || qType == 3 || qType == 4
                    ? sen.vnSentence
                    : qType == 6
                        ? sen.apply
                        : qType == 7
                            ? sen.content.replaceAll("_", " ")
                            : ""
            }</span>`) +
        (qType == 5
            ? ` <div class="question-sound play-sound">
                            <i class="fa-solid fa-volume-high"></i>
                            <audio
                              src="${sen.senAudio}"
                            ></audio>
                            <span>______________?</span>
                          </div>`
            : "") +
        (qType == 8
            ? `<span class="content-right-answer" style="display:none;"
                            >${sen.content.replaceAll("_", " ")}</span
                          >`
            : qType == 7 || qType == 5
                ? `<span class="content-right-answer hidden"
                            >${sen.vnSentence}</span
                          >`
                : `<span class="content-right-answer hidden"
                            >${sen.content.replaceAll("_", " ")}</span
                          >`) +
        `
                        </div>
                      </div>
                    </div>
                  </div>` +
        (aType === "write"
            ? ` <div class="test-exercise-answer answer-write">
                    <div class="answer-cover">
                      <input
                        class="answer-writing"
                        placeholder="Gõ đáp án của bạn tại đây"
                        name=""
                      />
                      <div class="answer-check">Kiểm tra</div>
                    </div>
                  </div>`
            : aType === "choose"
                ? ` <div class="test-exercise-answer">
                    <div class="answer-wrapper" answer-index="${answerIndex}">
                      <div class="answer-choice" value="1">
                        <span>${
                    qType == 2 ||
                    qType == 5 ||
                    qType == 6 ||
                    qType == 7 ||
                    qType == 8
                        ? answerChoices[0].replaceAll("_", " ")
                        : ""
                }</span>
                      </div>
                      <div class="answer-choice" value="2">
                        <span>${
                    qType == 2 ||
                    qType == 5 ||
                    qType == 6 ||
                    qType == 7 ||
                    qType == 8
                        ? answerChoices[1].replaceAll("_", " ")
                        : ""
                }</span>
                      </div>
                      <div class="answer-choice" value="3">
                        <span>${
                    qType == 2 ||
                    qType == 5 ||
                    qType == 6 ||
                    qType == 7 ||
                    qType == 8
                        ? answerChoices[2].replaceAll("_", " ")
                        : ""
                }</span>
                      </div>
                      <div class="answer-choice" value="4">
                        <span>${
                    qType == 2 ||
                    qType == 5 ||
                    qType == 6 ||
                    qType == 7 ||
                    qType == 8
                        ? answerChoices[3].replaceAll("_", " ")
                        : ""
                }</span>
                      </div>
                    </div></div>`
                : `<div class="test-exercise-answer answer-shuffle">
                    <div class="answer-cover">
                      <ul class="shuffle-content">${htmlShuffle}
                       
                      </ul>
                      <div class="answer-submit">Kiểm tra</div>
                    </div>
                  </div>`) +
        ` </div>`;

    return html;
}

// Render riêng cho câu hỏi loại 8: có thêm contexts trong nội dung câu hỏi

function renderContextQuestion() {
    const $questionHasContexts = $(".test-exercise[typequestion=8]");
    $questionHasContexts.each(function (index, question) {
        let senId = $(question).attr("id-sen");
        getContextContent(senId);
    });
}

async function getContextContent(senId) {
    try {
        let res = await axios.get(
            `/api/sentence/${senId}/context`
        );
        console.log(res.data);

        let html = renderContextHtmlQuestion(res.data);

        $(`.test-exercise[typequestion=8][id-sen=${senId}] .context-list`).append(
            html
        );
    } catch (error) {
        console.log(error);
    }
}

function renderContextHtmlQuestion(arr) {
    let html = "";
    arr.forEach((element) => {
        let separatedContent = [];
        if (element.containKey) {
            separatedContent = element.enSentence.split("_");
        }
        html +=
            ` <div class="context-item">
                            <div class="context-left">
                              <img
                                src="${element.img}.jpg"
                                alt=""
                              />
                            </div>
                            <div class="context-right">
                              <p>
                                <strong>Sara</strong>:
                                <span>
                                  ` +
            (element.containKey
                ? `${separatedContent[0]} <span class="sp-blank"></span
                                > ${separatedContent[2]}`
                : element.enSentence) +
            `</span
                                >
                              </p>
                            </div>
                          </div>`;
    });
    return html;
}

// Render 4 đáp án cho câu hỏi lựa chọn

function createAnswerChoice(answerIndex, sen, senFeature) {
    let count = 0;
    let wrongAnswers = sens.filter((sentence) => {
        if (count < 3 && sentence.id != sen.id) {
            count++;
            return true;
        }
        return false;
    });

    let wrongchoices = wrongAnswers.map((answer) => {
        return answer[senFeature];
    });

    let answerChoices = [];
    for (let i = 1; i <= 4; i++) {
        if (i == answerIndex) {
            answerChoices.push(sen[senFeature]);
        } else {
            let rd = Math.floor(Math.random() * wrongchoices.length);
            answerChoices.push(wrongchoices[rd]);
            wrongchoices.splice(rd, 1);
        }
    }

    return answerChoices;
}

function allowShuffle() {
    $(".shuffle-content").each(function (index, content) {
        $(content).sortable();
    });
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
            timeOut();

            // clearInterval(timeCount);
        }
    }, 1000);
}

async function timeOut() {
    await stopCountDown();
    await disableChoosing();
    await disableChecking();
    await disableSubmitting();
    await colectTestResult(false, "");
    await showAnswer();
    await nextQuestion();
}

function stopCountDown() {
    window.clearInterval(timeCount);
}

// Run test ---------------------------------------------------------------------------------------------------------------------------

function updateTestProgress() {
    const $exercises = $(".test-exercise");
    const n = $exercises.length;
    $(".progress-title span").text(n);
    // const $cur = $(".test-exercise.exercise-current");
    let indexCur = 0;
    $exercises.each(function (index, exercise) {
        if ($(exercise).hasClass("exercise-current")) {
            indexCur = index + 1;
        }
    });

    $(".progress-title strong").text(indexCur);
    $(".test-progress-bar").css({
        width: (indexCur / n) * 100 + "%",
    });
}

// Show answer sau khi submit ứng vs từng câu hỏi
function showAnswer() {
    const $cur = $(".test-exercise.exercise-current");
    const $question = $cur.find(".content-right .content-right-question");
    const $answer = $cur.find(".content-right .content-right-answer");
    const $audio = $cur.find(".test-exercise-question-sound audio");
    setTimeout(function () {
        $cur.addClass("finish");
        $question.addClass("hidden");
        $answer.removeClass("hidden");
        playASound($audio);
    }, 500);
}

function runTest() {
    getReadyToRun($(".test-exercise.exercise-current"));
    countdown(20);
    updateTestProgress();
    checkAnswerGuessing();
    activeSubmit();
    checkAnswerWriting();
    checkAnswerShuffling();

    //  getReadyToRun();
    $(".shuffle-content").each(function (index, content) {
        $(content).sortable({
            cursor: "move",
        });
    });
}

// Chuyển sang câu hỏi tiếp theo
function nextQuestion() {
    const $cur = $(".test-exercise.exercise-current");
    const $next = $cur.next();

    if ($next.length != 0) {
        setTimeout(function () {
            $cur.removeClass("exercise-current");
            $next.addClass("exercise-current");
            const $sound = $(".test-exercise.exercise-current").find(
                ".content-right audio"
            );
            if ($sound.length > 0) {
                playASound($sound);
            }
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

// Gửi danh sách kết quả kiểm tra câu
async function postTestResult(obj) {
    try {
        $("#loadMe").modal("show");
        let res = await axios.post(
            `/api/sentence-test-result?id=${topicId}`,
            obj
        );
        window.location.href = `/result?id=${topicId}`;
    } catch (error) {
        console.log(error);
    }
}

// Set up đầu cho câu hỏi current:
// để sẵn con trỏ chuột vào ô input nếu có
// Phát audio của câu hỏi nếu có

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
// Kiểm tra đáp án câu hỏi lựa chọn
function checkAnswerGuessing() {
    const $cur = $(".test-exercise.exercise-current");
    const answerIndex = parseInt(
        $cur.find(".answer-wrapper").attr("answer-index")
    );
    const $allChoice = $cur.find(".answer-choice");
    const $rightChoice = $cur.find(`.answer-choice[value=${answerIndex}]`);
    const $wrongChoice = $cur.find(`.answer-choice:not([value=${answerIndex}])`);

    $rightChoice.on("click", function () {
        stopCountDown();
        $rightChoice.addClass("choose-right");
        $cur
            .find(".test-exercise-question-content .img-success")
            .toggleClass("hidden");
        $cur
            .find(".test-exercise-question-content .img-person")
            .toggleClass("hidden");

        colectTestResult(true, $rightChoice.find("span").text());

        playSoundRight();
        showAnswer();
        disableChoosing();
        nextQuestion();
    });

    $wrongChoice.each(function (index, choice) {
        $(choice).on("click", function () {
            stopCountDown();
            $rightChoice.addClass("choose-right");
            $cur
                .find(".test-exercise-question-content .img-fail")
                .toggleClass("hidden");
            $cur
                .find(".test-exercise-question-content .img-person")
                .toggleClass("hidden");

            $(choice).addClass("choose-wrong");

            colectTestResult(false, $(choice).find("span").text());
            playSoundWrong();
            showAnswer();
            disableChoosing();
            nextQuestion();
        });
    });
}

// Khóa lựa chọn cho đáp án câu hỏi lựa chọn

function disableChoosing() {
    const $allChoice = $(".test-exercise.exercise-current .answer-choice");
    $allChoice.each(function (index, option) {
        $(option).css({
            "pointer-events": "none",
        });
    });
}

// Khóa click phím kiểm tra cho câu hỏi điền từ

function disableChecking() {
    const $allChoice = $(".test-exercise.exercise-current .answer-check");
    $allChoice.each(function (index, option) {
        $(option).css({
            "pointer-events": "none",
        });
    });
}

// Khóa click btn submit cho câu hỏi trộn

function disableSubmitting() {
    const $allChoice = $(".test-exercise.exercise-current .answer-submit");
    $allChoice.each(function (index, option) {
        $(option).css({
            "pointer-events": "none",
        });
    });
}

// Kiểm tra đáp án câu hỏi điền
function checkAnswerWriting() {
    const $cur = $(".test-exercise.exercise-current");
    const answer = $cur.find(".content-right-answer").text();
    const $input = $cur.find(".answer-writing");
    const $btn = $cur.find(".answer-check");
    $btn.on("click", function () {
        let submittedAnswer = $input.val();
        if (submittedAnswer.toLowerCase().trim() == answer.toLowerCase()) {
            playSoundRight();
            $input.addClass("show-answer-right");
            $cur
                .find(".test-exercise-question-content .img-success")
                .toggleClass("hidden");
            $cur
                .find(".test-exercise-question-content .img-person")
                .toggleClass("hidden");
            colectTestResult(true, submittedAnswer);
        } else {
            playSoundWrong();
            $input.addClass("show-answer-wrong");
            $cur
                .find(".test-exercise-question-content .img-fail")
                .toggleClass("hidden");
            $cur
                .find(".test-exercise-question-content .img-person")
                .toggleClass("hidden");
            colectTestResult(false, submittedAnswer);
        }
        stopCountDown();
        showAnswer();
        nextQuestion();
    });
}

// Kiểm tra đáp án câu hỏi shuffle
function checkAnswerShuffling() {
    const $cur = $(".test-exercise.exercise-current");
    const answer = $(
        ".test-exercise.exercise-current .content-right-answer"
    ).text();
    const $btn = $cur.find(".answer-submit");

    $btn.on("click", function () {
        const $words = $cur
            .find(".test-exercise-answer.answer-shuffle .answer-cover>ul>li")
            .toArray();

        let words = $words.map(function (word, index) {
            return $(word).text();
        });
        let submitAnswer = words.join(" ").trim();
        console.log(submitAnswer);
        if (answer === submitAnswer) {
            playSoundRight();

            $cur
                .find(".test-exercise-question-content .img-success")
                .toggleClass("hidden");
            $cur
                .find(".test-exercise-question-content .img-person")
                .toggleClass("hidden");
            colectTestResult(true, submitAnswer);
        } else {
            playSoundWrong();

            $cur
                .find(".test-exercise-question-content .img-fail")
                .toggleClass("hidden");
            $cur
                .find(".test-exercise-question-content .img-person")
                .toggleClass("hidden");
            colectTestResult(false, submitAnswer);
        }
        $btn.css({
            "pointer-events": "none",
            color: "var(--color-text-light)",
            background: "var(--color-white)",
            border: "1px solid var(--color-border-gray-light)",
            "border-bottom": "3px solid var(--color-border-gray-light)",
        });
        stopCountDown();
        showAnswer();
        nextQuestion();
    });
}

// Lấy kết quả Test cho từng câu hỏi sau mỗi làn trả lời

function colectTestResult(status, userAnswer) {
    const $cur = $(".test-exercise.exercise-current");
    let time = 20 - parseInt($(".test-countdown-number").text());
    answerRequest.push({
        questionTitle: $cur.find(".question-title").text(),
        questionContent: $cur.find(".content-right-question").text(),
        answer: $cur.find(".content-right-answer").text(),
        userAnswer: userAnswer,
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
