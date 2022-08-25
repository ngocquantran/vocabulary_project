$(function () {
    chooseImage();
    playWordSound();
    $(".save-vocab").on("click", generateEditRequest)
    document.querySelector(".word-audio-input").addEventListener('change', handleWordAudioSelect, false);
    document.querySelector(".sen-audio-input").addEventListener('change', handleSenAudioSelect, false);
})

function handleWordAudioSelect(evt) {
    let file = evt.target.files[0];
    let reader = new FileReader();
    reader.onload = (function (theFile) {
        return function (e) {
            $(".word-audio audio").attr("src", e.target.result);
        }
    })(file);
    reader.readAsDataURL(file);
}

function handleSenAudioSelect(evt) {
    let file = evt.target.files[0];
    let reader = new FileReader();
    reader.onload = (function (theFile) {
        return function (e) {
            $(".sen-audio audio").attr("src", e.target.result);
        }
    })(file);
    reader.readAsDataURL(file);
}


function chooseImage() {
    const $imageInput = $(".update-photo input");
    $imageInput.change(function () {
        console.log($imageInput.val());
        const reader = new FileReader();
        reader.addEventListener("load", () => {
            let uploaded_image = reader.result;
            $(".vocab-photo .photo img").attr("src", `${uploaded_image}`);
        });
        reader.readAsDataURL(this.files[0]);
    });
}


function generateEditRequest() {
    let img = $(".update-photo input").prop("files")[0];
    console.log(img);
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
    let audio = $(".word-audio-input").prop("files")[0];
    console.log(audio);
    let senAudio = $(".sen-audio-input").prop("files")[0];
    console.log(senAudio);

    if (!img || word.length == 0
        || phonetic.length == 0
        || enMeaning.length == 0
        || vnMeaning.length == 0
        || $(".en-sen textarea").eq(0).val().length == 0 || $(".en-sen textarea").eq(1).val().length == 0 || $(".en-sen textarea").eq(2).val().length == 0
        || vnSentence.length == 0
        || !audio
        || !senAudio) {
        alert("Bạn cần nhập đủ thông tin");


    } else {
        let sure = confirm("Bạn chắc chắn muốn thêm không?");
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
            console.log(request);
            const formData = new FormData();
            formData.append("vocab",new Blob([JSON.stringify(request)], {
                type: "application/json"
            }));

            formData.append("img", img);
            formData.append("audio", audio);
            formData.append("senAudio", senAudio);
            uploadAddVocabRequest(formData);
        }
    }
}

async function uploadAddVocabRequest(formData) {
    try {
        let res = await axios.post("/admin/api/add-vocab", formData, {
            headers: {
                Accept: 'application/json',
                "Content-Type": "multipart/form-data;",
            },
        });
        console.log("ok rồi");
        window.location.href="/admin/vocabs";

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











