$(function () {
    renderOrders();
    pagination();
    $("#button-clear").on("click", function () {
        window.location.href = "/admin/orders";
    });
    $("#button-search").on("click", searchKeyword);

    $(".search-user").on("keyup", function (e) {
        if (e.keyCode == 13 && $(this).val().length > 0) {
            $("#button-search").trigger("click");
        }

    });
    renderStatusButton();



});


function renderOrders() {
    const $container = $(".table");
    $container.html("");
    let html = "";
    html += `<thead>
                  <tr>
                    <th class="align-top">Mã đơn hàng</th>
                    <th class="align-top">Ngày đặt hàng</th>
                    <th class="align-top">Họ tên</th>
                    <th class="align-top">Email</th>
                    <th class="align-top">Sản phẩm</th>
                    ${status == "PENDING"
        ? ' <th class="align-top">Hành động</th>'
        : status == "ACTIVATED"
            ? ' <th class="align-top">Ngày kích hoạt</th>'
            : '<th class="align-top">Ngày kích hoạt</th><th class="align-top">Ngày hết hạn</th>'
    }
                    
                  </tr>
                  </thead>`
    pageInfo.dataList.forEach(order => {
        let date = new Date(order.activeDate);
        let nextDate=new Date();
        nextDate.setDate(date.getDate()+parseInt(order.aPackage.duration)*30);
        html += `<tr>
                    <td>${order.id}</td>
                    <td>${order.orderDate}</td>
                    <td>${order.user.fullName}</td>
                    <td>${order.user.email}</td>
                    <td>${order.aPackage.title}</td>`+(
                     status == "PENDING"
            ?    `<td><button type="button" class="btn btn-danger active-order" id-order="${order.id}">Kích hoạt</button></td>`
            : status == "ACTIVATED"
                ? ` <td class="align-top">${order.activeDate}</td>`
                : `<td class="align-top">${order.activeDate}</td><th class="align-top">${nextDate}</th>`
        )
    });
    $container.append(html);
    activeOrder();
}


function pagination(){
    let $container=$("#pagination");
    $container.pagination({
        dataSource: function(done){
            var result = [];
            for (var i = 1; i <= pageInfo.totalElements; i++) {
                result.push(i);
            }
            done(result);
        },
        className: 'paginationjs-theme-blue paginationjs-big',
        pageSize:5,
        pageNumber: pageInfo.currentPage,

    });
    renderPaginationjs();

}

function renderPaginationjs(){
    const $pages=$(".paginationjs-pages>ul>li");
    $pages.each((index,page)=>{
        const $link=$(page).find("a");
        const pageNum=$(page).attr("data-num");
        if (!$(page).hasClass("disabled")){
            $link.on("click",function (){
                window.location.href=`/admin/orders?status=${status}&pageNum=${pageNum}${keyword ? '&keyword=' + keyword : ''}`
            })
        }

    })
}

// function renderPagination() {
//     const $container = $("ul.pagination");
//     $container.html("");
//     let previousPage = parseInt(pageInfo.currentPage) - 1;
//     let html = `<li class="paginate_button page-item previous ${pageInfo.currentPage == 1 ? 'disabled' : ''}" id="example2_previous">
//                     <a href="${pageInfo.currentPage == 1 ? '#' : '/admin/orders?status='+status+'&pageNum=' + previousPage}${keyword ? '&keyword=' + keyword : ''}" aria-controls="example2"  class="page-link">Previous</a>
//                   </li>`;
//     for (let i = 1; i <= pageInfo.totalPages; i++) {
//         html += `<li class="paginate_button page-item ${pageInfo.currentPage == i ? 'active' : ''}">
//                     <a href="${pageInfo.currentPage == i ? '#' : '/admin/orders?status='+status+'&pageNum=' + i}${keyword ? '&keyword=' + keyword : ''}" aria-controls="example2"  class="page-link">${i}</a>
//                   </li>`
//     }
//
//     let nextPage = parseInt(pageInfo.currentPage) + 1;
//     html += `<li class="paginate_button page-item next ${pageInfo.currentPage == pageInfo.totalPages || pageInfo.dataList.length==0 ? 'disabled' : ''}" id="example2_next">
//                     <a href="${pageInfo.currentPage == pageInfo.totalPages ? '#' : '/admin/orders?status='+status+'&pageNum=' + nextPage}${keyword ? '&keyword=' + keyword : ''}" aria-controls="example2"  class="page-link">Next</a>
//                   </li>`;
//     $container.append(html);
//
// }

function searchKeyword() {
    const $input = $(".search-user");
    if ($input.val().length > 0) {
        window.location.href = `/admin/orders?status=${status}&keyword=${$input.val().trim()}`;
    }
}

function renderStatusButton(){
    const $btnStatus=$(".btn-status");
    $btnStatus.each((index,btn)=>{
        let value=$(btn).attr("status-value");
        if (value==status){
            $(btn).addClass("btn-primary disabled");
        }else {
            $(btn).addClass("btn-outline-primary");
        }
        $(btn).attr("href",`/admin/orders?status=${value}`)

    })
}

function activeOrder(){
    $(".active-order").each((index,button)=>{
        $(button).on("click",function (){
            postActiveRequest($(this).attr("id-order"));
        })
    })
}

async function postActiveRequest(orderId){
    try {
        let res=await axios.post(`/admin/api/active-order?id=${orderId}`);
        window.location.reload();
    }catch (e) {
        console.log(e);
    }
}