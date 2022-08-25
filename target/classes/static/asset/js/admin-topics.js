$(function () {
    renderCourses();

    $("#button-clear").on("click", function () {
        window.location.href = "/admin/topics";
    });
    $("#button-search").on("click", searchKeyword);

    $(".search-topic").on("keyup", function (e) {
        if (e.keyCode == 13 && $(this).val().length > 0) {
            $("#button-search").trigger("click");
        }

    });
    pagination();
});


function renderCourses() {
    const $container = $(".table-topic-content");
    $container.html("");
    let html = "";
    pageInfo.dataList.forEach(topic => {
        html += `<tr class="text-center">
                  <td>${topic.id}</td>
                  <td>${topic.title}</td>
                  <td>${topic.numberOfVocabs>0?'Từ vựng':'Mẫu câu'}</td>
                  <td>${topic.course?topic.course.title:'Chưa gán khóa học'}</td>
         
                  <td>
                    <a href="/admin/topic-detail?id=${topic.id}" type="button" class="btn btn-outline-primary">
                      Xem
                    </a>
                  </td>
                </tr>`
    });
    $container.append(html);
}

// function renderPagination() {
//     const $container = $("ul.pagination");
//     $container.html("");
//     let previousPage = parseInt(pageInfo.currentPage) - 1;
//     let html = `<li class="paginate_button page-item previous ${pageInfo.currentPage == 1 ? 'disabled' : ''}" id="example2_previous">
//                     <a href="${pageInfo.currentPage == 1 ? '#' : '/admin/topics?pageNum=' + previousPage}${keyword ? '&keyword=' + keyword : ''}" aria-controls="example2"  class="page-link">Previous</a>
//                   </li>`;
//     for (let i = 1; i <= pageInfo.totalPages; i++) {
//         html += `<li class="paginate_button page-item ${pageInfo.currentPage == i ? 'active' : ''}">
//                     <a href="${pageInfo.currentPage == i ? '#' : '/admin/topics?pageNum=' + i}${keyword ? '&keyword=' + keyword : ''}" aria-controls="example2"  class="page-link">${i}</a>
//                   </li>`
//     }
//
//     let nextPage = parseInt(pageInfo.currentPage) + 1;
//     html += `<li class="paginate_button page-item next ${pageInfo.currentPage == pageInfo.totalPages || pageInfo.dataList.length==0? 'disabled' : ''}" id="example2_next">
//                     <a href="${pageInfo.currentPage == pageInfo.totalPages ? '#' : '/admin/topics?pageNum=' + nextPage}${keyword ? '&keyword=' + keyword : ''}" aria-controls="example2"  class="page-link">Next</a>
//                   </li>`;
//     $container.append(html);
//
// }

function searchKeyword() {
    const $input = $(".search-topic");
    if ($input.val().length > 0) {
        window.location.href = `/admin/topics?keyword=${$input.val().trim()}`;
    }

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
                window.location.href=`/admin/topics?pageNum=${pageNum}${keyword ? '&keyword=' + keyword : ''}`
            })
        }

    })
}