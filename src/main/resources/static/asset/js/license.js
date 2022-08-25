$(function () {
    renderOrders();
    switchChangeOption();
    viewOrderDetail();
    renderActiveOrders();
});


function switchChangeOption() {
    const $options = $(".change-option .option");
    $options.each((index, option) => {
        $(option).on("click", function () {
            $options.each((index, op) => {
                $(op).removeClass("on");
            });
            $(this).addClass("on");
            switch ($(this).text()) {
                case "Khóa học của tôi":
                    $(".user-course").css({
                        display: "block",
                    });
                    $(".user-order").css({
                        display: "none",
                    });
                    break;
                case "Đơn hàng của tôi":
                    $(".user-course").css({
                        display: "none",
                    });
                    $(".user-order").css({
                        display: "block",
                    });
                    break;
            }
        });
    });
}

function renderOrders() {
    const $container = $(".user-order .table-group-divider");
    $container.html("");
    let html = "";
    orders.forEach(order => {
        let date = new Date(order.orderDate);


        html += `<tr>
              <td>${order.id}</td>
              <td>${order.aPackage.title}</td>
              <td>${date.getDate()}/${date.getMonth()+1}/${date.getFullYear()}</td>
              <td>${order.status === "PENDING" ? "Chưa thanh toán" : "Đã thanh toán"}</td>
              <td class="view-order" data-bs-toggle="modal" data-bs-target="#orderDetail" id-order="${order.id}">Xem chi tiết</td>

            </tr>`
    });
    $container.append(html);
}

function renderActiveOrders(){
    const $container = $(".user-course .table-group-divider");
    $container.html("");
    let html = "";
    activeOrders.forEach(order => {
        let date = new Date(order.activeDate);

        let nextDate=new Date();
        nextDate.setDate(date.getDate()+parseInt(order.aPackage.duration)*30);

        let today=new Date();

        html += `<tr>
              <td>${order.aPackage.title}</td>
              <td>${parseInt(order.aPackage.duration)*30} ngày</td>
              <td>${date.getDate()}/${date.getMonth()+1}/${date.getFullYear()}</td>
              <td>${nextDate.getDate()}/${nextDate.getMonth()+1}/${nextDate.getFullYear()}</td>
              <td>${today>=nextDate?"EXPIRED":"ACTIVATED"}</td>
            </tr>`
    });
    $container.append(html);
}

function viewOrderDetail() {
    const $btnViewOrder = $(".user-order .view-order");
    $btnViewOrder.each((index, btn) => {


        $(btn).on("click", function () {
            let idOrder = $(btn).attr("id-order");
            let order = orders.find(o => {
                return o.id == idOrder;
            })

            let price = parseInt(order.aPackage.pricePerMonth) * parseInt(order.aPackage.duration)
            let formatprice = formatCash(price.toString());

            $("#orderDetail .user-info .name").val(order.user.fullName);
            $("#orderDetail .user-info .email").val(order.user.email);
            $("#orderDetail .user-info .phone").val(order.user.phone);
            $("#orderDetail .bill-package strong").text(order.aPackage.title);
            $("#orderDetail .bill-package span").text(formatprice + "đ");
            $("#orderDetail .bill-value span").eq(1).text(formatprice + "đ");
            $("#orderDetail .bill-total .total").text(formatprice + "đ");

        })

    })
}

function formatCash(str) {
    return str.split('').reverse().reduce((prev, next, index) => {
        return ((index % 3) ? next : (next + ',')) + prev
    })
}
