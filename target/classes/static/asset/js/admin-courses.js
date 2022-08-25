$(function () {
    renderCourses();
    pagination();
    $("#button-clear").on("click", function () {
        window.location.href = "/admin/courses";
    });
    $("#button-search").on("click", searchKeyword);

    $(".search-course").on("keyup", function (e) {
        if (e.keyCode == 13 && $(this).val().length > 0) {
            $("#button-search").trigger("click");
        }

    });
});


function renderCourses() {
    const $container = $(".table-courses-content");
    $container.html("");
    let html = "";
    pageInfo.dataList.forEach(course => {
        html += `<tr class="text-center">
                  <td>${course.id}</td>
                  <td>${course.title}</td>
                  <td>${course.category.title}</td>
                  <td>${course.group.title}</td>
                  <td>${course.levels.map(l => l.title)}</td>
                  <td>${course.status=="PUBLIC"?"CÔNG KHAI":"ẨN"}</td>
                  <td>
                    <a href="/admin/course-detail?id=${course.id}" type="button" class="btn btn-outline-primary">
                      Xem
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
                window.location.href=`/admin/courses?pageNum=${pageNum}${keyword ? '&keyword=' + keyword : ''}`
            })
        }

    })
}

// function renderPagination() {
//     const $container = $("ul.pagination");
//     $container.html("");
//     let previousPage = parseInt(pageInfo.currentPage) - 1;
//     let html = `<li class="paginate_button page-item previous ${pageInfo.currentPage == 1 ? 'disabled' : ''}" id="example2_previous">
//                     <a href="${pageInfo.currentPage == 1 ? '#' : '/admin/courses?pageNum=' + previousPage}${keyword ? '&keyword=' + keyword : ''}" aria-controls="example2"  class="page-link">Previous</a>
//                   </li>`;
//     for (let i = 1; i <= pageInfo.totalPages; i++) {
//         html += `<li class="paginate_button page-item ${pageInfo.currentPage == i ? 'active' : ''}">
//                     <a href="${pageInfo.currentPage == i ? '#' : '/admin/courses?pageNum=' + i}${keyword ? '&keyword=' + keyword : ''}" aria-controls="example2"  class="page-link">${i}</a>
//                   </li>`
//     }
//
//     let nextPage = parseInt(pageInfo.currentPage) + 1;
//     html += `<li class="paginate_button page-item next ${pageInfo.currentPage == pageInfo.totalPages || pageInfo.dataList.length==0? 'disabled' : ''}" id="example2_next">
//                     <a href="${pageInfo.currentPage == pageInfo.totalPages ? '#' : '/admin/courses?pageNum=' + nextPage}${keyword ? '&keyword=' + keyword : ''}" aria-controls="example2"  class="page-link">Next</a>
//                   </li>`;
//     $container.append(html);
//
// }

function searchKeyword() {
    const $input = $(".search-course");
    if ($input.val().length > 0) {
        window.location.href = `/admin/courses?keyword=${$input.val().trim()}`;
    }

}