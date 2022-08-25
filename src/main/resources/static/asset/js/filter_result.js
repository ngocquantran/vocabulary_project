$(function () {


  renderListVocab(vocabsToChoose);

  renderGraph();
  chooseWords();
  $(".choose-word-btn").on("click", handleSubmitBtn);
  getCourseIdFromTopic(topicCourse);


});

const URL_API = "http://localhost:8898/api/v1";
let params = new URLSearchParams(window.location.search);
let topicId = params.get("id");
let userId = "1";
let known = 0;
let unknown = 0;
let learnRequest = [];

//render back button

function getCourseIdFromTopic(topicCourse) {
  $(".header-menu-item.menu-item-back a").attr(
      "href",
      `/course?id=${topicCourse.course.id}`
  );
  $(".header-menu-item.menu-item-back a span").text(
      topicCourse.title)
}

// RENDER PAGE FILTER-----------------------------------------------


function renderListVocab(vocabArr) {
  const $container = $(".choose-study-word-content-body .row");
  $container.html("");
  let html = "";
  vocabArr.forEach((vocab) => {
    //đếm known và unknown để render circle graph
    if (vocab.status) {
      known++;
    } else {
      unknown++;
    }
    //Khởi tạo list từ chọn để học
    learnRequest.push({
      vocabId: vocab.vocabId,
      learn:false
    })

    //Render danh sách từ
    html += `<div class="col col-lg-6">
                      <div class="choose-study-word-item ${
                        vocab.status == false ? "item-unknown" : ""
                      }" id-vocab="${vocab.vocabId}">
                        <img src="${vocab.img}" alt="">
                        <label>
                          <p class="choose-study-word-item-text">${
                            vocab.word
                          } <span>${vocab.type}</span></p>
                          <input type="checkbox" name="" id="" value="abide-by">
                          <span class="custom-checkbox">
                            <span class="icon-all"></span>
                          </span>
                        </label>
                      </div>
                    </div>`;
  });
  $container.append(html);
}

function renderGraph() {
  const $container = $(".filter-result-right-circle");
  $container.html("");
  let html = ` <canvas id="filter-result-right-circle-chart" style="width: 160px; height: 160px;"></canvas>
                <script>
                  var xValues = ["Biết", "Chưa biết"];
                  var yValues = [${known}, ${unknown}];
                  var barColors = ["#58CC02", "rgb(255, 1, 78)"];

                  new Chart("filter-result-right-circle-chart", {
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
  $container.append(html);
  $(".filter-result-right-know span").text(known);
  $(".filter-result-right-un-know span").text(unknown);
}

function handleSubmitBtn() {
  getListVocabsRequest();
  postListRequestAndGoToLearn(learnRequest);
}


function getListVocabsRequest() {
  learnRequest.forEach((vocab) => {
    const $vocab = $(`.choose-study-word-item[id-vocab="${vocab.vocabId}"]`);
    let $checkItem = $vocab.find("input:checked");
    if ($checkItem.length > 0) {
      vocab.learn = true;
    } else {
      vocab.learn = false;
    }
  });
}

async function postListRequestAndGoToLearn(obj) {
  try {
    console.log(obj);
    let res = await axios.post(
      `/api/filter-result?id=${topicId}`,
      obj
    );
    window.location.href = `/learn/vocab?id=${topicId}`;
  } catch (error) {
    console.log(error);
  }
}

// MP3 handle-------------------------------------------------------

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

function playASound($sound) {
  $sound[0].load();
  $sound[0].onloadeddata = function () {
    $sound[0].play();
  };
}

// Choose word to study after filter----------------------------

function chooseWords() {
  const $btnAllWords = $(".choose-study-word-btn.all-word");
  const $btnUnKnownWords = $(".choose-study-word-btn.unknown-word");

  $btnAllWords.on("click", function () {
    chooseAllWords();
    updateStudyBtn();
  });

  $btnUnKnownWords.on("click", function () {
    chooseUnKnownWords();
    updateStudyBtn();
  });

  const $words = $(".choose-study-word-item label");
  $words.each(function (index, word) {
    $(word).on("click", function () {
      updateStudyBtn();
    });
  });
}

function chooseAllWords() {
  const $wordInputs = $(".choose-study-word-item label input");
  $wordInputs.each(function (index, input) {
    $(input).prop("checked", true);
  });
}

function chooseUnKnownWords() {
  const $checkedInput = $(".choose-study-word-item label input:checked");
  $checkedInput.each(function (index, input) {
    $(input).prop("checked", false);
  });

  const $unknownWords = $(".choose-study-word-item.item-unknown label input");
  $unknownWords.each(function (index, input) {
    $(input).prop("checked", true);
  });
}

function updateStudyBtn() {
  const $wordInputs = $(".choose-study-word-item label input:checked");
  const $activeBtn = $(
    ".choose-study-word-content-footer .choose-word-btn.btn-choose-active"
  );
  const $inactiveBtn = $(
    ".choose-study-word-content-footer .choose-word-btn:not(.btn-choose-active)"
  );
  if ($wordInputs.length == 0) {
    $activeBtn.removeClass("btn-choose-active");
  } else {
    $inactiveBtn.addClass("btn-choose-active");
  }
}
