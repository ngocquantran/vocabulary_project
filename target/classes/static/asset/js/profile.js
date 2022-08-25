$(function () {
    renderInfo();
    chooseImage();
    switchChangeOption();
    $(".user-pass .info-header button").on("click", preCheckPassword);
    $(".user-info .info-header button").on("click",updateUserInfo)
});

function renderInfo() {
    $(".info-avatar img").attr("src", user.avatar);
    $(".info-item .info-name").val(user.fullName);
    $(".info-item .info-email").val(user.email);
    $(".info-item .info-number").val(user.phone);
}


function chooseImage() {
    const $imageInput = $(".update input");
    $imageInput.change(function () {
        console.log($imageInput.val());
        const reader = new FileReader();
        reader.addEventListener("load", () => {
            let uploaded_image = reader.result;
            $(".info-avatar img").attr("src", `${uploaded_image}`);
        });
        reader.readAsDataURL(this.files[0]);
    });
}

function switchChangeOption() {
    const $options = $(".change-option .option");
    $options.each((index, option) => {
        $(option).on("click", function () {
            $options.each((index, op) => {
                $(op).removeClass("on");
            });
            $(this).addClass("on");
            switch ($(this).text()) {
                case "Thông tin chung":
                    $('.user-info').show();
                    $(".user-pass").hide();
                    break;
                case "Mật khẩu":
                    $('.user-info').hide();
                    $(".user-pass").show();
                    break;
            }
        })
    })
}


function preCheckPassword() {
    const $error = $(".user-pass .error");
    const $oldPass = $(".old-password");
    const $newPass = $(".new-password");
    const $confirmPass = $(".confirm-password");

    if ($oldPass.val().length === 0 || $newPass.val().length === 0 || $confirmPass.val().length === 0) {
        $error.show();
        $error.text("Vui lòng nhập đầy đủ thông tin.");
    } else if ($newPass.val() !== $confirmPass.val()) {
        $error.show();
        $error.text("Mật khẩu mới không khớp.");
    } else if ($newPass.val().length < 8 || $newPass.val().length > 16) {
        $error.show();
        $error.text("Mật khẩu dài từ 8 đến 16 kí tự.");
    } else {
        updatePassword();
    }


}

async function updatePassword() {
    try {
        let res = await axios.post("/api/user/updatePassword", {
            oldPassword: $(".old-password").val(),
            newPassword: $(".new-password").val()
        });

        $(".user-pass .error").hide();
        $(".user-pass .success").show();
        $(".old-password").val("");
        $(".new-password").val("");
        $(".confirm-password").val("");

    } catch (error) {
        $(".user-pass .error").show();
        $(".user-pass .error").text(error.response.data.message);

    }
}

async function uploadImg() {
    try {
        const formData = new FormData();
        const imgFile = $(".info-item-content .update input").prop("files")[0];
        formData.append("file", imgFile);
        let res = await axios.post("/api/user/updateAvatar", formData, {
            headers: {
                "Content-Type": "multipart/form-data",
            },
        })
    }catch (error){
        console.log(error);
    }
}

async function updateUserInfo(){
    try {
        const $fullName=$(".user-info .info-name");
        const $phone=$(".user-info .info-number");
        if ($fullName.val().length===0||$phone.val().length===0){
            $(".user-info .error").show();
        }else {
            $(".user-info .error").hide();

            const formData = new FormData();
            formData.append("fullName",$fullName.val());
            formData.append("phone",$phone.val());

            const imgFile = $(".info-item-content .update input").prop("files")[0];
            formData.append("file", imgFile);
            let res = await axios.post("/api/user/updateUserInfo", formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
            });
            setTimeout(() => {
                window.location.reload();
            }, 1000);
        }


    }catch (error){
        console.log(error);
    }
}
