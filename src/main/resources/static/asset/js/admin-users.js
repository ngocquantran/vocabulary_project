$(function () {
    renderUsers();
    pagination();
    $("#button-clear").on("click", function () {
        window.location.href = "/admin/users";
    });
    $("#button-search").on("click", searchKeyword);

    $(".search-user").on("keyup", function (e) {
        if (e.keyCode == 13 && $(this).val().length > 0) {
            $("#button-search").trigger("click");
        }

    });
});


function renderUsers() {
    const $container = $(".table-user-content");
    $container.html("");
    let html = "";
    pageInfo.dataList.forEach(user => {
        html += ` <tr class="text-center">
                  <td>${user.id}</td>
                  <td>${user.email}</td>
                  <td>${user.fullName}</td>
                  <td>${user.userRoles.map(r=>r.role.name)}</td>
               
                  <td>
                    <a href="/admin/user-detail?id=${user.id}"
                            type="button"
                            class="btn btn-outline-primary"
                    >
                      Chi tiết
                    </a>
                  </td>
                </tr>`
    });
    $container.append(html);
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
                window.location.href=`/admin/users?pageNum=${pageNum}${keyword ? '&keyword=' + keyword : ''}`
            })
        }

    })
}

// function renderPagination() {
//     const $container = $("ul.pagination");
//     $container.html("");
//     let previousPage = parseInt(pageInfo.currentPage) - 1;
//     let html = `<li class="paginate_button page-item previous ${pageInfo.currentPage == 1 ? 'disabled' : ''}" id="example2_previous">
//                     <a href="${pageInfo.currentPage == 1 ? '#' : '/admin/users?pageNum=' + previousPage}${keyword ? '&keyword=' + keyword : ''}" aria-controls="example2"  class="page-link">Previous</a>
//                   </li>`;
//     for (let i = 1; i <= pageInfo.totalPages; i++) {
//         html += `<li class="paginate_button page-item ${pageInfo.currentPage == i ? 'active' : ''}">
//                     <a href="${pageInfo.currentPage == i ? '#' : '/admin/users?pageNum=' + i}${keyword ? '&keyword=' + keyword : ''}" aria-controls="example2"  class="page-link">${i}</a>
//                   </li>`
//     }
//
//     let nextPage = parseInt(pageInfo.currentPage) + 1;
//     html += `<li class="paginate_button page-item next ${pageInfo.currentPage == pageInfo.totalPages || pageInfo.dataList.length==0? 'disabled' : ''}" id="example2_next">
//                     <a href="${pageInfo.currentPage == pageInfo.totalPages ? '#' : '/admin/users?pageNum=' + nextPage}${keyword ? '&keyword=' + keyword : ''}" aria-controls="example2"  class="page-link">Next</a>
//                   </li>`;
//     $container.append(html);
//
// }

function searchKeyword() {
    const $input = $(".search-user");
    if ($input.val().length > 0) {
        window.location.href = `/admin/users?keyword=${$input.val().trim()}`;
    }

}