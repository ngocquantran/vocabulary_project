$(function () {
    login();
    signUp();
    $(".submit-reset").on("click", submitResetPassword)
    $(".switch").on("click", function () {
        $(".login-form").toggle();
        $(".signup-form").toggle();
    });

    $(".login-form .email-group input").focus(function () {
        $(".login-form .error").slideUp();
        $(".spinner-signin .spinner-border").hide();
    });

    $(".login-form .password-group input").focus(function () {
        $(".login-form .error").slideUp();
        $(".spinner-signin .spinner-border").hide();
    })

    $(".signup-form .name-group input").focus(function () {
        $(".signup-form .error").slideUp();
        $(".spinner-signup .spinner-border").hide();
    })
    $(".signup-form .email-group input").focus(function () {
        $(".signup-form .error").slideUp();
        $(".spinner-signup .spinner-border").hide();
    })
    $(".signup-form  .password-group input").focus(function () {
        $(".signup-form .error").slideUp();
        $(".spinner-signup .spinner-border").hide();
    })


});

function login() {
    const $emailLogin = $(".login-form .email-group input");
    const $passLogin = $(".login-form .password-group input");
    const $btnLogin = $(".login-form button");

    $btnLogin.on("click", async () => {
        try {
            $(".spinner-signin .spinner-border").show();
            //Check trước khi gửi
            if ($emailLogin.val().length == 0 || $passLogin.val().length == 0) {
                $(".login-form .error").text("* Bạn chưa nhập đủ thông tin");
                $(".login-form .error").slideDown();
            } else {
                let res = await axios.post("/api/auth/login", {
                    email: $emailLogin.val().trim(),
                    password: $passLogin.val().trim()
                });
                console.log(res.data);
                let roles = res.data.map(role => role.authority);
                console.log(roles);
                if (roles.includes("ROLE_ADMIN")) {
                    window.location.href = "/admin/orders";
                } else {
                    window.location.href = "/";
                }
                console.log("ok rồi");
                $(".spinner-signin .spinner-border").hide();
            }

        } catch (error) {
            console.log(error);
            $(".spinner-signin .spinner-border").hide();
            $(".login-form .error").text("* Email hoặc mật khẩu không chính xác");
            $(".login-form .error").slideDown();
        }
    })
}

function signUp() {
    const $nameSignUp = $(".signup-form .name-group input");
    const $emailSignUp = $(".signup-form .email-group input");
    const $passSignUp = $(".signup-form  .password-group input");
    const $btnSignUp = $(".signup-form button");

    $btnSignUp.on("click", async () => {
        try {
            $(".spinner-signup .spinner-border").show();
            //check lỗi trước khi gửi đăng ký
            if ($nameSignUp.val().length == 0 || $emailSignUp.val().length == 0 || $passSignUp.val().length == 0) {
                $(".signup-form .error").text("* Bạn chưa nhập đủ thông tin");
                $(".signup-form .error").slideDown();
            } else if (containsSpecialChars($nameSignUp.val())) {
                $(".signup-form .error").text("* Họ tên không được chứa ký tự đặc biệt");
                $(".signup-form .error").slideDown();
            } else if (!validateEmail($emailSignUp.val())) {
                $(".signup-form .error").text("* Email không hợp lệ");
                $(".signup-form .error").slideDown();
            } else {
                await axios.post("/api/auth/signup", {
                    fullName: $nameSignUp.val().trim(),
                    email: $emailSignUp.val().trim(),
                    password: $passSignUp.val().trim()
                });

                $nameSignUp.val("");
                $emailSignUp.val("");
                $passSignUp.val("");

                console.log("ok rồi");

                $(".spinner-signup .spinner-border").hide();
                $(".signup-success").text("* Vui lòng xác nhận nội dung email đã được gửi đến địa chỉ email của bạn (lưu ý có thể nằm trong hộp thư rác)");
                $(".signup-success").slideDown();
            }


        } catch (error) {
            console.log(error);
            $(".spinner-signup .spinner-border").hide();
            $(".signup-form .error").text("* " + error.response.data.message);
            $(".signup-form .error").slideDown();

        }
    });
}

function containsSpecialChars(str) {
    const specialChars = /[`!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]/;
    return specialChars.test(str);
}

function validateEmail(email) {
    let filter = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
    if (filter.test(email)) {
        return true;
    }
    return false;
}

async function submitResetPassword() {
    try {
        $(".spinner-reset").show();
        let email = $(".email-reset").val();
        if (email.length == 0) {
            alert("Bạn chưa nhập email");
            return;
        }
        if (!validateEmail(email)) {
            alert("Email không hợp lệ");
            return;
        }
        let res = await axios.post(`/api/forgot-password?email=${email}`);
        alert("Vui lòng kiểm tra email để lấy mật khẩu mới!");
        $(".spinner-reset").hide();
        $(".email-reset").val("");


    } catch (e) {
        console.log(e);
    }

}







