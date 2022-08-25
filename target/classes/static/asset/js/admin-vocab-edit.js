$(function () {
    renderVocabInfo();
    playWordSound();
    $(".save-vocab").on("click", generateEditRequest)
})

function renderVocabInfo() {
    $(".vocab-photo img").attr("src", vocab.img);
    $(".word textarea").val(vocab.word);

    //thể loại
    const $typeContainer = $(".type select");
    let typeHtml = ` <option value="(n)" ${vocab.type == "(n)" ? "selected" : ""}>(n)</option>
                   <option value="(v)" ${vocab.type == "(v)" ? "selected" : ""}>(v)</option>
                   <option value="(adj)" ${vocab.type == "(adj)" ? "selected" : ""}>(adj)</option>
 <option value="(adv)" ${vocab.type == "(adv)" ? "selected" : ""}>(adv)</option>`;
    $typeContainer.append(typeHtml);

    $(".phonetic textarea").val(vocab.phonetic);
    $(".en-meaning textarea").val(vocab.enMeaning);
    $(".vn-meaning textarea").val(vocab.vnMeaning);
    $(".en-sen textarea").eq(0).val(vocab.enSentence.split("_")[0]);
    $(".en-sen textarea").eq(1).val(vocab.enSentence.split("_")[1]);
    $(".en-sen textarea").eq(2).val(vocab.enSentence.split("_")[2]);
    $(".vn-sen textarea").val(vocab.vnSentence);


}


function generateEditRequest() {
    let word = escapeHtml($(".word textarea").val().trim());
    console.log(word);
    let type = $(".type select").val();
    console.log(type);
    let phonetic = escapeHtml($(".phonetic textarea").val().trim());
    console.log(phonetic);
    let enMeaning = escapeHtml($(".en-meaning textarea").val().trim());
    console.log(enMeaning);
    let vnMeaning = escapeHtml($(".vn-meaning textarea").val().trim());
    console.log(vnMeaning);
    let enSentence = escapeHtml($(".en-sen textarea").eq(0).val().trim() + " _" + $(".en-sen textarea").eq(1).val().trim() + "_ " + $(".en-sen textarea").eq(2).val().trim())
    console.log(enSentence);
    let vnSentence = escapeHtml($(".vn-sen textarea").val().trim());
    console.log(vnSentence);

    if (word.length == 0 || phonetic.length == 0 || enMeaning.length == 0 || vnMeaning.length == 0 || enSentence.length == 0 || vnSentence.length == 0) {
        alert("Bạn cần nhập đủ thông tin");

    } else {
        let sure = confirm("Bạn chắc chắn muốn điều chỉnh không?");
        if (sure) {
            let request = {
                word,
                type,
                phonetic,
                enMeaning,
                vnMeaning,
                enSentence,
                vnSentence
            };
            postEditRequest(request);
        }


    }

}

async function postEditRequest(request) {
    try {
        let res = await axios.post(`/admin/api/edit-vocab?id=${vocab.id}`, request);
        console.log("ok rồi");
        window.location.href = "/admin/vocabs";
    } catch (e) {
        console.log(e);
    }
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


let entityMap = {
    '<': '&lt;',
    '>': '&gt;',
    '&': '&amp;',
    '=': '&equals;'
}

function escapeHtml(string) {
    return String(string).replace(/[<>&=]/g, function (s) {
        return entityMap[s];
    })
}