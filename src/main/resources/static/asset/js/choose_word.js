
  async function initChooseWord() {
    await getListOfVocabs();
    chooseWords();
    $(".choose-word-btn").on("click", handleSubmitBtn);
  }
   
    


let learnRequest = [];
// Filter học lại

async function getListOfVocabs() {
  try {
    let res = await axios.get(
      `/api/topic-vocab-to-learn?id=${topicId}`
    );
    console.log(res.data);
    renderListVocab(res.data);
  } catch (error) {
    console.log(error);
  }
}

function renderListVocab(vocabArr) {
  const $container = $(".choose-study-word-content-body .row");
  $container.html("");
  let html = "";
  vocabArr.forEach((vocab) => {
    //Khởi tạo list từ chọn để học
    learnRequest.push({
      vocabId: vocab.vocabId,
      learn: false,
    });

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
