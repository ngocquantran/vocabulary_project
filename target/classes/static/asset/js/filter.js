$(function () {
    renderTopicVocabs(vocabs);
    renderFilterList(vocabs);
    filterVocab();
    playWordSound();
    getCourseIdFromTopic(topicCourse);

});

// RENDER PAGE ----------------------------------------------------------

let filteredVocabs = [];

//render back button

function getCourseIdFromTopic(topicCourse) {
    $(".header-menu-item.menu-item-back a").attr(
        "href",
        `/course?id=${topicCourse.course.id}`
    );
    $(".header-menu-item.menu-item-back a span").text(
        topicCourse.title)
}

function renderFilterList(arr) {
    filteredVocabs = [];
    arr.forEach((element) => {
        filteredVocabs.push({
            vocabId: element.id,
            status: false,
        });
    });
}

function renderTopicVocabs(arr) {
    const $filterContent = $(".filter-word-content");
    $filterContent.html("");
    let html = "";
    arr.forEach(function (word) {
        html += `<div class="filter-word-layer layer right" id-word="${word.id}">
                <div class="filter-word-layer-viewport">
                  <p class="filter-word-item-name">${word.word}</p>
                  <p class="filter-word-item-type">${word.type}</p>
                  <div class="filter-word-item-sound play-sound">
                     <i class="fa-solid fa-volume-high"></i>
                    <audio src="${word.audio}"></audio>
                  </div>
                </div>
              </div>`;
    });
    $filterContent.append(html);
}

// Filter word choice---------------------------------------------------

function filterVocab() {
    let index = 0;
    const $cards = $(".filter-word-layer.right");
    setCurrentFilterCart($cards.eq(0));

    const n = $cards.length;

    $(".filter-word-progress-title span").text(`${n}`);

    updateFilterVocabProgress(
        n,
        index,
        $(".filter-word-progress-range span"),
        $(".filter-word-progress-title strong")
    );

    const $btn = $(".filter-word-bottom-btn");
    $btn.on("click", function () {
        if ($(this).hasClass("btn-yes")) {
            filteredVocabs[index].status = true;
        }
        console.log(filteredVocabs);

        $btn.css({"pointer-events": "none"});
        setTimeout(() => {
            $btn.css({"pointer-events": "all"});
        }, 1000);

        if (index < n - 1) {
            $(".filter-word-layer.right.hide").removeClass("hide");

            $cards.eq(index).addClass("hide");
            $cards.eq(index).removeClass("show");
            $cards.eq(index).removeClass("layer-current");
            index++;
            updateFilterVocabProgress(
                n,
                index,
                $(".filter-word-progress-range span"),
                $(".filter-word-progress-title strong")
            );

            setCurrentFilterCart($cards.eq(index));
        } else {

            postFilterResult(filteredVocabs);
        }
    });
}

// Gửi danh sách lọc từ vựng sau khi lọc
async function postFilterResult(obj) {
    try {
        $("#loadMe").modal("show");
        let res = await axios.post(`/api/filter?id=${topicId}`,
            obj
        );
        window.location.href = `/filter-result?id=${topicId}`;
    } catch (error) {
        console.log(error);
    }
}

function setCurrentFilterCart($card) {
    $card.addClass("show");
    $card.addClass("layer-current");
    const $sound = $card.find(".filter-word-item-sound audio");
    playASound($sound);
}

function updateFilterVocabProgress(
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
