$(function () {
    renderVocabPicture(vocabs);
    selectCategory();
    renderVocabSelection();
    $(".save-topic").on("click", generateData)

})

let img = "";

function renderVocabPicture(arr) {
    const $container = $(".picture-wrapper>.row");
    $container.html("");
    let html = "";
    arr.forEach(picture => {
        html += ` <div class="col-lg-2 col-md-3 col-6">
                                <label class="picture-content">
                                    <img alt="" src="${picture.img}">
                                    <p>${picture.word}</p>
                                    <input checked="" id-word="${picture.id}" img-src="${picture.img}" name="picture-selection"
                                           type="radio">
                                </label>
                            </div>`
    });
    $container.append(html);
    chooseImage();
}

function renderSentencePicture(arr) {
    const $container = $(".picture-wrapper>.row");
    $container.html("");
    let html = "";
    arr.forEach(picture => {
        html += ` <div class="col-lg-2 col-md-3 col-6">
                                <label class="picture-content">
                                    <img alt="" src="${picture.img}">
                                    <p class="text-center" >${picture.content.replaceAll('_', ' ')}</p>
                                    <input checked="" id-word="${picture.id}" img-src="${picture.img}" name="picture-selection"
                                           type="radio">
                                </label>
                            </div>`
    });
    $container.append(html);
    chooseImage();
}

function selectCategory() {
    $(".select-category").change(function () {
        $(".course-photo img").attr("src","");
        if ($(this).val() == "Từ vựng") {
            renderVocabPicture(vocabs);
            renderVocabSelection();
        } else {
            renderSentencePicture(sentences);
            renderSentenceSelection();
        }
    })

}

function chooseImage() {
    const $pictures = $(".picture-content");
    $pictures.each((index, picture) => {
        $(picture).on("click", function () {
            img = $(".picture-content input[name=picture-selection]:checked").attr("img-src");
            $(".course-photo img").attr("src", img);
        })
    })
}

function renderVocabSelection() {
    const $container = $(".select-content");
    $container.html("");
    let html = "";
    vocabs.forEach(vocab => {
        html += `  <option value="${vocab.id}">${vocab.word}-${vocab.vnMeaning}</option>`
    });
    $container.append(html);
}

function renderSentenceSelection() {
    const $container = $(".select-content");
    $container.html("");
    let html = "";
    sentences.forEach(sentence => {
        html += `  <option value="${sentence.id}">${sentence.content.replaceAll('_', ' ')}</option>`
    });
    $container.append(html);
}

function generateData() {
    let img = $(".course-photo img").attr("src");
    console.log(img);
    let title = escapeHtml($(".title textarea").val().trim());
    console.log(title);
    let content = $(".select-content").select2("data").map(d=>d.id);
    console.log(content);
    let type = $(".select-category").val();
    console.log(type);

    if (img.length == 0 || title.length == 0 || content.length == 0) {
        alert("Bạn cần nhập đủ thông tin");
        return;
    }
    let request = {
        img,
        title,
        content,
        type
    }
    postAddTopicRequest(request);


}

async function postAddTopicRequest(request) {
    try {
        let res = await axios.post("/admin/api/add-topic", request);
        console.log("ok rồi");

    } catch (e) {
        console.log(e);
    }
}


let entityMap = {
    '<': '&lt;',
    '>': '&gt;',
    '&': '&amp',
    '/': '&frasl;',
    '=': '&equals;'
}

function escapeHtml(string) {
    return String(string).replace(/[<>&/=]/g, function (s) {
        return entityMap[s];
    })
}