$(function () {
    renderPackage();
    choosePackage();
    order();
});

function formatCash(str) {
    return str.split('').reverse().reduce((prev, next, index) => {
        return ((index % 3) ? next : (next + ',')) + prev
    })
}

function renderPackage() {
    $(".register-name").val(user.fullName);
    $(".register-number").val(user.phone);
    $(".register-email").val(user.email);

    const $container = $(".package-list .row");
    $container.html("");
    let html = "";
    packages.forEach(package => {

        let pricePerMonth = formatCash(package.pricePerMonth.toString());

        let totalprice = formatCash((parseInt(package.pricePerMonth) * parseInt(package.duration)).toString());
        html += ` <div class="col col-lg-4 col-md-6">
                            <div class="package-item item-${package.type}">
                                <div class="item-header">
                                    <h3>${package.duration} tháng</h3>
                                    <h6>
                                        ${package.description}
                                    </h6>
                                </div>
                                <div class="item-body">
                                    <div class="fee">Học phí:</div>
                                    <div class="fee-content">
                                        <strong>${pricePerMonth}đ</strong><span>/tháng</span>
                                    </div>
                                    <ul class="detail">
                                        <li>Thanh toán: <span>${totalprice}</span>đ</li>
                                        <li>Không giới hạn khóa học</li>
                                    </ul>
                                    <div class="item-choose">
                                        <a href="#register-group" title="${package.title}" id-package="${package.id}"
                                        >Chọn gói</a
                                        >
                                    </div>
                                </div>
                            </div>
                        </div>`
    });
    $container.append(html);
}

function choosePackage() {
    const $packages = $(".package-item");

    $packages.each((index, package) => {
        let $btn = $(package).find(".item-choose a");
        $btn.on("click", function () {

            $packages.each((index, p) => {
                $(p).find(".item-choose a").removeClass("active");
            });
            $btn.addClass("active");
            let color = $btn.css("background-color");

            $(".chosed-item").text(
                `Tài khoản học cao cấp PREMIUM ${$(package)
                    .find(".item-header h3")
                    .text()}`
            );
            $(".chosed-item").css({color: color})
        });
    });
}

function order() {
    $(".order").on("click", () => {
        const $chosed = $(".item-choose a.active");
        if (!$chosed.length) {
            alert("Vui lòng chọn khóa học");
        } else if ($(".register-number").val().length==0) {
            alert("Vui lòng cập nhật số điện thoại trong thông tin cá nhân");
        } else {

            submitOrder();
        }
    })
}

async function submitOrder() {
    try {
        if (isOrderPendingExist){
            alert("Bạn đã đặt hàng trước đó. Vui lòng chờ đơn hàng cũ được kích hoạt.");
            return;
        }
        let idPackage = parseInt($(".item-choose a.active").attr("id-package"));
        let res = await axios.post(`/api/user/order?id=${idPackage}`);
        let idorder = parseInt(res.data)

        window.location.href = `/user/payment?id=${idorder}`;

    } catch (error) {
        console.log(error);
    }
}
