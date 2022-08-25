$(function () {

  getCourseIdFromTopic();
  getTestResultOfNowStage();
  renderUserRank(userRank);
  getTopicRecords();
  renderNotification(state);
  reLearningVocabs();
  renderTop3Ranks();
  renderTop8Ranks();
  async function init() {
    await getAllComment();
    submitCommentLevel1();
    inputCommentLevel1();
    rotateVocabCard();
  }
  init();

  const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]')
  const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl))


});


let userId = "1";

let known = 0,
  unknown = 0,
  totalTime = 0;

let keyAPI = "";
let comments = [];

let entityMap={
  '<':'&lt;',
  '>':'&gt;',
  '&':'&amp',
  '"':'&quot;',
  "'":"&#x27;",
  '/':'&frasl;',
  '=':'&equals;'
}

function escapeHtml(string){
  return String(string).replace(/[<>&"'/=]/g,function (s) {
    return entityMap[s];
  })
}



// RENDER PAGE INFORMATION-----------------------------------------------------------------------------------------------

//render back button

 function getCourseIdFromTopic() {
    $("a.header-menu-item").attr(
      "href",
      `/course?id=${topicCourse.course.id}`
    );
    const $btnLearning = $(".btn-learning");
    const $btnTesting = $(".btn-testing");
    switch (topicCourse.course.category.id) {
      case 1:
        keyAPI = "vocabs";
        $btnLearning.attr("data-bs-target", "#choosewordmodel");
        $btnLearning.attr("data-bs-toggle", "modal");
        $btnTesting.attr("href", `/test/vocab?id=${topicId}`);
        break;
      case 2:
        keyAPI = "sens";
        $btnLearning.attr("data-bs-target", "");
        $btnLearning.attr("data-bs-toggle", "");
        $btnLearning.attr("href", `/learn/sentence?id=${topicId}`);
        $btnTesting.attr("href", `/test/sentence?id=${topicId}`);
        break;
    }
    console.log(keyAPI);

}



//Lấy dữ liệu userTopicVocab từ database để render kết quả test
function getTestResultOfNowStage() {
    renderCircleGrahp();
    if (keyAPI === "vocabs") {
      renderVocabSummary();
    } else {
      renderSenSummary();
    }

}

//Render User rank

function renderUserRank(rank) {
  $(".user-rank-item .avatar img").attr("src",rank.userImg);
  $(".user-rank-item .rank").text(rank.rank);
  $(".user-rank-item .name").text(rank.userName);
  $(".user-rank-item .right").text(`${rank.rightAnswers} từ đúng`);
  $(".user-rank-item .time").text(`Thời gian ${rank.testTime}s`);
}

// Render biểu đồ hình tròn: thuộc-chưa thuộc
function renderCircleGrahp() {
  const $graphContainer = $(".result-evaluate-circle-right-wrapper");
  $graphContainer.html("");
  let html = "";

  testResults.forEach((result) => {
    if (result.status) {
      known++;
    } else {
      unknown++;
    }
  });
  html = ` <span class="result-evaluate-circle-percent">${Math.floor(
    (known / (known + unknown)) * 100
  )}%</span>
                    <canvas
                      id="result-evaluate-circle-chart"
                      style="width: 120px; height: 120px"
                    ></canvas>
                    <script>
                      var xValues = ["Thuộc", "Chưa thuộc"];
                      var yValues = [${known}, ${unknown}];
                      var barColors = ["#58CC02", "rgb(255, 1, 78)"];

                      new Chart("result-evaluate-circle-chart", {
                        type: "doughnut",
                        data: {
                          labels: xValues,
                          datasets: [
                            {
                              backgroundColor: barColors,
                              data: yValues,
                            },
                          ],
                        },
                        options: {
                          plugins: {
                            legend: {
                              display: false,
                            },
                          },
                        },
                      });
                    </script>`;
  $graphContainer.append(html);

  $(".result-evaluate-circle-know span").text(known);
  $(".result-evaluate-circle-un-know span").text(unknown);
}

function renderTestTime(arrRecord) {
  arrRecord.forEach((record) => {
    if (record.stage === "NOW") {
      $(".result-evaluate-circle-desc span").text(record.testTime);
    }
  });
}

// Render list từ vựng đã thuộc và chưa thuộc
function renderVocabSummary() {
  $(".sen-summary").css({
    display: "none",
  });
  $(".vocab-group.forget .vocab-group-header span").text(unknown);
  $(".vocab-group.remember .vocab-group-header span").text(known);
  const $forgetContainer = $(".vocab-group.forget .vocab-list .row");
  const $rememberContainer = $(".vocab-group.remember .vocab-list .row");
  $forgetContainer.html("");
  $rememberContainer.html("");

  testResults.forEach((result) => {
    let html = `  <div class="col ">
                    <div class="vocab-item">
                      <div class="vocab-item-viewport">
                        <div class="item-front">
                          <img
                            class="item-image"
                            src="${result.vocab.img}"/>
                          <p class="item-text">${result.vocab.word}</p>
                          <p class="item-pronounce">${result.vocab.phonetic}</p>

                          <span class="rotate"
                            ><i class="fa-solid fa-rotate"></i
                          ></span>
                        </div>
                        <div class="item-back">
                          <div class="item-back-cover">
                            <audio
                              src="${result.vocab.audio}"
                            ></audio>
                            <p class="item-type">${result.vocab.type}</p>
                            <p class="item-text-vi">${result.vocab.vnMeaning}</p>
                            <span class="rotate"
                              ><i class="fa-solid fa-rotate"></i
                            ></span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>`;
    if (result.status) {
      $rememberContainer.append(html);
    } else {
      $forgetContainer.append(html);
    }
  });
}

function renderSenSummary() {
  $(".vocab-summary").css({
    display: "none",
  });
  $(".sen-group.forget .sen-group-header span").text(unknown);
  $(".sen-group.remember .sen-group-header span").text(known);
  const $forgetContainer = $(".sen-group.forget .sen-list");
  const $rememberContainer = $(".sen-group.remember .sen-list");
  $forgetContainer.html("");
  $rememberContainer.html("");
  testResults.forEach((result, index) => {
    let html =
      `   <div class="sen-item">
                <h4>Câu hỏi ${index + 1}: ${result.questionTitle}</h4>
                <div class="question-content">
                  ${result.questionContent}
                </div>
                <div class="right-answer-content">
                  <h5>Đáp án đúng:</h5>
                  <div class="answer-content">
                    ${result.answer}
                  </div>
                </div>` +
      (result.status
        ? ""
        : `<div class="wrong-answer-content">
                  <h5>Đáp án của bạn:</h5>
                  <div class="answer-content">
                    ${result.userAnswer}
                  </div>
                </div>`) +
      `</div>`;
    if (result.status) {
      $rememberContainer.append(html);
    } else {
      $forgetContainer.append(html);
    }
  });
}

// HIển thị thông báo



function renderNotification(state) {
  const $notifyTitle = $(".result-message-notify");
  const $notifyDesc = $(".result-message-desc");
  let bestRecord = userTopicRecords.filter((record) => {
    return record.stage === "BEST";
  });
  let nowRecord = userTopicRecords.filter((record) => {
    return record.stage === "NOW";
  });
  if (state === "PASS") {
    if (bestRecord.length == 0) {
      //Pass lần đầu
      $notifyTitle.text("Bạn đã vượt qua chủ đề từ vựng này");
      $notifyDesc.text(
        "Bài mới đã được mở, bạn có thể học ngay. Tuy nhiên, hãy kiểm tra lại để có kết quả tốt nhất"
      );
    } else {
      if (bestRecord.rightAnswers < testResults.length) {
        //Pass lần đầu do kết quả tốt nhất trước đó vẫn chưa pass
        $notifyTitle.text("Bạn đã vượt qua chủ đề từ vựng này");
        $notifyDesc.text(
          "Bài mới đã được mở, bạn có thể học ngay. Tuy nhiên, hãy kiểm tra lại để có kết quả tốt nhất"
        );
      } else {
        // Nếu đã từng pass trước đó
        if (nowRecord.rightAnswers < testResults.length) {
          //  lần test hiện tại không trả lời đúng 100%
          $notifyTitle.text("Bạn đã quên từ vựng");
          $notifyDesc.text("Hãy cải thiện để có kết quả cao hơn");
        } else if (nowRecord.testTime > bestRecord.testTime) {
          //  lần test hiện tại pass nhưng có thời gian chậm hơn
          $notifyTitle.text("Bạn đã vượt qua chủ đề từ vựng này");
          $notifyDesc.text(
            "Thành tích lần này của bạn chưa vượt qua thành tích cao nhất mà bạn đã từng đạt được, hãy cải thiện để có kết quả cao hơn!"
          );
        } else {
          //  lần test hiện tại có kết quả tốt nhất
          $notifyTitle.text("Bạn đã vượt qua chủ đề từ vựng này");
          $notifyDesc.text(
            "Bài mới đã được mở, bạn có thể học ngay. Tuy nhiên, hãy kiểm tra lại để có kết quả tốt nhất"
          );
        }
      }
    }
  } else {
    //Không pass chủ đề
    $notifyTitle.text("Bạn chưa vượt qua chủ đề này");
    $notifyDesc.text(
      "Để vượt qua, bạn cần đạt kết quả trả lời đúng 100%. Hãy học lại hoặc kiểm tra lại để có kết quả cao hơn."
    );
  }
}

// Lấy dữ liệu UserTopicRecord từ database để render Biểu đồ tiến độ học
 function getTopicRecords() {

    renderTestTime(userTopicRecords);
    renderRecordGraph(userTopicRecords);

}

// Render biểu đồ tiến độ học
function renderRecordGraph(arrRecord) {
  let timeArr = getdataFromRecordData(arrRecord, "testTime");
  let totalArr = getdataFromRecordData(arrRecord, "totalAnswers");
  let answerArr = getdataFromRecordData(arrRecord, "rightAnswers");
  let percent = [];
  for (let i = 0; i < arrRecord.length; i++) {
    percent.push(Math.floor((answerArr[i] / totalArr[i]) * 100));
  }
  percent[0] = 0;
  console.log(percent);
  var ctx = document
    .getElementById("result-evaluate-graph-chart")
    .getContext("2d");
  var myChart = new Chart(ctx, {
    type: "line",
    data: {
      labels:
        answerArr.length == 2
          ? ["Trước khi học", "Hiện tại"]
          : ["Trước khi học", "Tốt nhất", "Gần đây nhất", "Hiện tại"],
      datasets: [
        {
          label: ["% chính xác"], // Name the series
          data: percent, // Specify the data values array
          fill: false,
          borderColor: "#2196f3", // Add custom color border (Line)
          backgroundColor: "#2196f3", // Add custom color background (Points and Fill)
          borderWidth: 1, // Specify bar border width
        },
      ],
    },
    options: {
      plugins: {
        tooltip: {
          callbacks: {
            afterBody: function (context) {
              return (
                (keyAPI === "vocabs"
                  ? `\nThuộc ${answerArr[context[0].dataIndex]}/${
                      totalArr[context[0].dataIndex]
                    } từ`
                  : `\nĐúng ${answerArr[context[0].dataIndex]}/${
                      totalArr[context[0].dataIndex]
                    } câu`) + `\nThời gian: ${timeArr[context[0].dataIndex]}s`
              );
            },
          },
        },
      },
      responsive: true, // Instruct chart js to respond nicely.
      maintainAspectRatio: false, // Add to prevent default behaviour of full-width/height'

      scales: {
        y: { beginAtZero: true, max: 100 },
      },
    },
  });
}

// Sắp xếp lại thứ tự record First-Best-Previous-Now để render biểu đồ tiến độ
function getdataFromRecordData(arrRecord, character) {
  let arr = [];
  arrRecord.forEach((record) => {
    if (record.stage === "FIRST") {
      arr.push(record[character]);
    }
  });
  arrRecord.forEach((record) => {
    if (record.stage === "BEST") {
      arr.push(record[character]);
    }
  });
  arrRecord.forEach((record) => {
    if (record.stage === "PREVIOUS") {
      arr.push(record[character]);
    }
  });
  arrRecord.forEach((record) => {
    if (record.stage === "NOW") {
      arr.push(record[character]);
    }
  });
  return arr;
}



// Chức năng lật flashcard để xem thông tin từ vựng
function rotateVocabCard() {
  const $cards = $(".vocab-item");
  $cards.each(function (index, card) {
    const $cardView = $(card).find(".vocab-item-viewport");
    const $frontBtn = $(card).find(".item-front");
    const $backBtn = $(card).find(".item-back");
    $frontBtn.on("click", function () {
      $cardView.css({
        transform: "rotateY(-180deg)",
      });
      playASound($(card).find(".item-back audio"));
    });
    $backBtn.on("click", function () {
      $cardView.css({
        transform: "rotateY(0deg)",
      });
    });
  });
}

function playASound($sound) {
  $sound[0].load();
  $sound[0].onloadeddata = function () {
    $sound[0].play();
  };
}

async function getAllComment() {
  try {
    let res = await axios.get(`/api/topic-comments?id=${topicId}`);
    comments = res.data;
    console.log(comments);
    renderCommentSection(comments);
    toggleReply();
    inputCommentLevel2();

    submitCommentLevel2();
  } catch (error) {
    console.log(error);
  }
}

function renderCommentSection(arr) {
  const $container = $(".discuss-content");
  $container.html("");
  let html = "";
  arr.forEach((comment) => {
    html += `  <div class="discuss-item ${
      comment.idParent != null
        ? 'level-2"' + 'id-parent="' + comment.idParent + '"'
        : '"'
    } id="${comment.id}">
              <div class="item-avatar">
                <img src="${comment.userAvatar}" alt="" />
              </div>
              <div class="item-wrapper">
                <div class="item-head">
                  <span class="item-username">${comment.userName}</span>
                  <span class="item-timestamp">${comment.createdAt}</span>
                </div>
                <p class="item-discussion">${comment.message}</p>
                ${
                  comment.idParent == null
                    ? `<div class="item-reply">Trả lời</div> 
                     <div class="input-field comment reply" style="display:none">
            <textarea rows="4" placeholder="Viết bình luận của bạn"></textarea>
            <button id="` +
                      comment.id +
                      `">Gửi</button>`
                    : ""
                }
               
          </div>
              </div>
               
            </div>`;
  });
  $container.append(html);
}

function inputCommentLevel2() {
  const $inputFields = $(".input-field.comment.reply");

  $inputFields.each(function (index, field) {
    const $input = $(field).find("textarea");
    const $btn = $(field).find("button");
    $input.on("keydown", function (e) {
      if (e.keyCode === 13 && $input.val().trim().length > 0) {
        $input.blur();
        $btn.trigger("click");
        $btn.css({
          "pointer-events": "all",
          opacity: 0.5,
        });
      } else {
        if ($input.val().length > 0) {
          $btn.css({
            "pointer-events": "all",
            opacity: 1,
          });
        } else {
          $btn.css({
            "pointer-events": "none",
            opacity: 0.5,
          });
        }
      }
    });
  });
}

function inputCommentLevel1() {
  const $inputFields = $(".input-field.comment.level-1");
  const $input = $inputFields.find("textarea");
  const $btn = $inputFields.find("button");
  $input.on("keydown", function (e) {
    if (e.keyCode === 13 && $input.val().trim().length > 0) {
      $input.blur();

      $btn.trigger("click");
      $btn.css({
        "pointer-events": "all",
        opacity: 0.5,
      });
    } else {
      if ($input.val().length > 0) {
        $btn.css({
          "pointer-events": "all",
          opacity: 1,
        });
      } else {
        $btn.css({
          "pointer-events": "none",
          opacity: 0.5,
        });
      }
    }
  });
}

function submitCommentLevel2() {
  const $inputFields = $(".input-field.comment.reply");
  $inputFields.each(function (index, field) {
    const $input = $(field).find("textarea");
    const $btn = $(field).find("button");
    let id = $btn.attr("id");
    $btn.on("click", function () {
      let content=escapeHtml($input.val().trim())
      let comment = {
        message: `${content}`,
        idParent: id,
      };
      console.log(comment);
       createComment(comment);
      $input.val("");
    });
  });
}

function submitCommentLevel1() {
  const $inputField = $(".input-field.comment.level-1");
  const $input = $inputField.find("textarea");
  const $btn = $inputField.find("button");
  $btn.on("click", function () {
    let content=escapeHtml($input.val().trim())
    let comment = {
      message: `${content}`,
      idParent: null,
    };
    console.log(comment);
     createComment(comment);
    $input.val("");
  });
}

async function createComment(comment) {
  try {
    let res = await axios.post(
      `/api/topic-comments?id=${topicId}`,
      comment
    );
    getAllComment();
  } catch (error) {
    console.log(error);
  }
}

function toggleReply() {
  const $comments = $(".discuss-item");
  $comments.each(function (index, comment) {
    const $reply = $(comment).find(".item-reply");
    const $input = $(comment).find(".input-field.comment");
    $reply.on("click", function () {
      $input.toggle();
      $input.find("textarea").val("");
    });
  });
}

function renderTop3Ranks(){
   //Top1
  $(".content-item.item-1 .avatar img").attr("src",topRanks[0].userImg);
  $(".content-item.item-1 .name").text(topRanks[0].userName);
  $(".content-item.item-1 .position").attr("data-bs-title",`Từ đúng ${topRanks[0].rightAnswers} <br></span> Thời gian ${topRanks[0].testTime}s`);

  //Top2
  if (topRanks.length>=2){
    $(".content-item.item-2 .avatar img").attr("src",topRanks[1].userImg);
    $(".content-item.item-2 .name").text(topRanks[1].userName);
    $(".content-item.item-2 .position").attr("data-bs-title",`Từ đúng ${topRanks[1].rightAnswers} <br></span> Thời gian ${topRanks[1].testTime}s`);

  }

  //Top3
  if (topRanks.length>=3){
    $(".content-item.item-3 .avatar img").attr("src",topRanks[2].userImg);
    $(".content-item.item-3 .name").text(topRanks[2].userName);
    $(".content-item.item-3 .position").attr("data-bs-title",`Từ đúng ${topRanks[2].rightAnswers} <br></span> Thời gian ${topRanks[2].testTime}s`);

  }



}

function renderTop8Ranks(){
for (let i=4;i<=8;i++){
  if (topRanks.length>=i){
    let $item=$(`.rank-item.top-${i}`);
    $item.find(".avatar img").attr("src",topRanks[i-1].userImg);
    $item.find(".name").text(topRanks[i-1].userName);
    $item.find(".right").text(`Từ đúng ${topRanks[i-1].rightAnswers}`);
    $item.find(".time").text(`Thời gian ${topRanks[i-1].testTime}s`);
    //đã sửa

  }
}
}
