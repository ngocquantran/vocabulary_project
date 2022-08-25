$(function () {
    renderVocabs();
    pagination();
    $("#button-clear").on("click", function () {
        window.location.href = "/admin/vocabs";
    });
    $("#button-search").on("click", searchKeyword);

    $(".search-user").on("keyup", function (e) {
        if (e.keyCode == 13 && $(this).val().length > 0) {
            $("#button-search").trigger("click");
        }

    });

    learningViewportSlideBox();

    playWordSound();
});


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


//MP3-------------------------------------------------------------------------------------------------------------------------------


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


function renderVocabs() {
    const $container = $(".table-vocab-content");
    $container.html("");
    let html = "";
    pageInfo.dataList.forEach(vocab => {
        html += `  <tr class="text-center">
                  <td>${vocab.id}</td>
                  <td>${vocab.word}</td>
                  <td>${vocab.type}</td>
                  <td>${vocab.phonetic}</td>
                  <td>${vocab.vnMeaning}</td>
                  <td>
                    <button
                            type="button"
                            class="btn btn-outline-primary"
                            data-bs-toggle="modal"
                            data-bs-target="#previewModal"
                            id-word="${vocab.id}"
                            onclick="renderModal(${vocab.id})"
                    >
                      Xem
                    </button>
                  </td>
                  <td>
                    <a href="/admin/vocab-edit?id=${vocab.id}"
                            type="button"
                            class="btn btn-outline-primary"
                           
                    >
                      Chỉnh sửa
                    </a>
                  </td>
                </tr>`
    });
    $container.append(html);
}


function pagination() {
    let $container = $("#pagination");
    $container.pagination({
        dataSource: function (done) {
            var result = [];
            for (var i = 1; i <= pageInfo.totalElements; i++) {
                result.push(i);
            }
            done(result);
        },
        className: 'paginationjs-theme-blue paginationjs-big',
        pageSize: 5,
        pageNumber: pageInfo.currentPage,

    });
    renderPaginationjs();

}

function renderPaginationjs() {
    const $pages = $(".paginationjs-pages>ul>li");
    $pages.each((index, page) => {
        const $link = $(page).find("a");
        const pageNum = $(page).attr("data-num");
        if (!$(page).hasClass("disabled")) {
            $link.on("click", function () {
                window.location.href = `/admin/vocabs?pageNum=${pageNum}${keyword ? '&keyword=' + keyword : ''}`
            })
        }

    })
}

// function renderPagination() {
//   const $container = $("ul.pagination");
//   $container.html("");
//   let previousPage = parseInt(pageInfo.currentPage) - 1;
//   let html = `<li class="paginate_button page-item previous ${pageInfo.currentPage == 1 ? 'disabled' : ''}" id="example2_previous">
//                     <a href="${pageInfo.currentPage == 1 ? '#' : '/admin/vocabs?pageNum=' + previousPage}${keyword ? '&keyword=' + keyword : ''}" aria-controls="example2"  class="page-link">Previous</a>
//                   </li>`;
//   for (let i = 1; i <= pageInfo.totalPages; i++) {
//     html += `<li class="paginate_button page-item ${pageInfo.currentPage == i ? 'active' : ''}">
//                     <a href="${pageInfo.currentPage == i ? '#' : '/admin/vocabs?pageNum=' + i}${keyword ? '&keyword=' + keyword : ''}" aria-controls="example2"  class="page-link">${i}</a>
//                   </li>`
//   }
//
//   let nextPage = parseInt(pageInfo.currentPage) + 1;
//   html += `<li class="paginate_button page-item next ${pageInfo.currentPage == pageInfo.totalPages || pageInfo.dataList.length==0? 'disabled' : ''}" id="example2_next">
//                     <a href="${pageInfo.currentPage == pageInfo.totalPages ? '#' : '/admin/vocabs?pageNum=' + nextPage}${keyword ? '&keyword=' + keyword : ''}" aria-controls="example2"  class="page-link">Next</a>
//                   </li>`;
//   $container.append(html);
//
// }

function searchKeyword() {
    const $input = $(".search-user");
    if ($input.val().length > 0) {
        window.location.href = `/admin/vocabs?keyword=${$input.val().trim()}`;
    }

}

function renderModal(idVocab) {
    let vocab = pageInfo.dataList.find((value => value.id == idVocab));
    console.log(vocab);
    const $container = $(".modal-body");
    $container.html("");
    let splitSentence = vocab.enSentence.split("_");
    let html = `<div class="learning-word-content">
            <div class="learning-word-layer layer" words="">
              <div class="learning-word-viewport">
                <div
                        class="learning-word-viewport-container"
                        rotate-data="0"
                >
                  <div class="learning-word-viewport-slide slide1 on">
                    <div class="card-img">
                      <img
                              src="${vocab.img}"
                              alt=""
                      />
                    </div>
                    <div class="card-content">
                      <p class="card-content-title">Definition:</p>
                      <p class="card-content-text">
                        <span class="card-content-type">${vocab.type}</span> ${vocab.enMeaning}
                      </p>
                    </div>
                    <div class="card-turn">
                            <span class="turn-icon">
                              <span>1</span>
                              <i class="fa-solid fa-rotate"></i>
                            </span>
                    </div>
                  </div>

                  <div class="learning-word-viewport-slide slide2">
                    <div class="card-img">
                      <img
                              src="${vocab.img}"
                              alt=""
                      />
                    </div>

                    <div class="card-content">
                      <p class="card-content-text-main">
                        ${vocab.word} <span class="card-content-type">${vocab.type}</span>
                      </p>

                      <p class="card-content-phonetic">
                              <span class="play-sound">
                                <i class="fa-solid fa-volume-high"></i>
                                <audio
                                        src="${vocab.audio}"
                                ></audio>
                              </span>
                        <span>${vocab.phonetic}</span>
                      </p>

                      <div class="text-definition-vi">${vocab.vnMeaning}</div>
                    </div>

                    <div class="card-turn">
                            <span class="turn-icon">
                              <span>2</span>
                              <i class="fa-solid fa-rotate"></i>
                            </span>
                    </div>
                  </div>

                  <div class="learning-word-viewport-slide slide3">
                    <div class="card-img">
                      <img
                              src="${vocab.img}"
                              alt=""
                      />
                    </div>

                    <div class="card-content">
                      <div class="example-group">
                        <p class="card-content-example">
                                <span class="play-sound">
                                  <i class="fa-solid fa-volume-high"></i>
                                  <audio
                                          src="${vocab.senAudio}"
                                  ></audio>
                                </span>
                          <span class="example-en">
                                  <span>${splitSentence[0]} </span>
                                  <span>${splitSentence[1]}</span>
                                  <span>${splitSentence[2]}</span>
                                </span>
                        </p>
                        <p class="example-vi">
                          ${vocab.vnSentence}
                        </p>
                      </div>
                    </div>

                    <div class="card-turn">
                            <span class="turn-icon">
                              <span>3</span>
                              <i class="fa-solid fa-rotate"></i>
                            </span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="dimmer"></div>
            </div>
          </div>`;
    $container.append(html);
    learningViewportSlideBox();
    playWordSound();

}
