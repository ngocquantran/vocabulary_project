$(function () {
    renderSentences();
    pagination();
    $("#button-clear").on("click", function () {
        window.location.href = "/admin/sentences";
    });
    $("#button-search").on("click", searchKeyword);

    $(".search-sentence").on("keyup", function (e) {
        if (e.keyCode == 13 && $(this).val().length > 0) {
            $("#button-search").trigger("click");
        }

    });
});


function renderSentences() {
    const $container = $(".table-sentence-content");
    $container.html("");
    let html = "";
    pageInfo.dataList.forEach(sen => {
        html += ` <tr class="text-center">
                  <td>${sen.id}</td>
                  <td>${sen.content.replaceAll("_"," ")}</td>
                  <td>${sen.phonetic.replaceAll("_"," ")}</td>
                  <td>${sen.vnSentence}</td>

                  <td>
                    <a href="/admin/sentence-detail?id=${sen.id}"
                            type="button"
                            class="btn btn-outline-primary"
                    >
                      Xem
                    </a>
                  </td>
                  <td>
                    <button
                            type="button"
                            class="btn btn-outline-primary"
                            onclick="alert('Chức năng này đang được cập nhật!')"
                    >
                      Chỉnh sửa
                    </button>
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
                window.location.href=`/admin/sentences?pageNum=${pageNum}${keyword ? '&keyword=' + keyword : ''}`
            })
        }

    })
}

// function renderPagination() {
//     const $container = $("ul.pagination");
//     $container.html("");
//     let previousPage = parseInt(pageInfo.currentPage) - 1;
//     let html = `<li class="paginate_button page-item previous ${pageInfo.currentPage == 1 ? 'disabled' : ''}" id="example2_previous">
//                     <a href="${pageInfo.currentPage == 1 ? '#' : '/admin/sentences?pageNum=' + previousPage}${keyword ? '&keyword=' + keyword : ''}" aria-controls="example2"  class="page-link">Previous</a>
//                   </li>`;
//     for (let i = 1; i <= pageInfo.totalPages; i++) {
//         html += `<li class="paginate_button page-item ${pageInfo.currentPage == i ? 'active' : ''}">
//                     <a href="${pageInfo.currentPage == i ? '#' : '/admin/sentences?pageNum=' + i}${keyword ? '&keyword=' + keyword : ''}" aria-controls="example2"  class="page-link">${i}</a>
//                   </li>`
//     }
//
//     let nextPage = parseInt(pageInfo.currentPage) + 1;
//     html += `<li class="paginate_button page-item next ${pageInfo.currentPage == pageInfo.totalPages || pageInfo.dataList.length==0? 'disabled' : ''}" id="example2_next">
//                     <a href="${pageInfo.currentPage == pageInfo.totalPages ? '#' : '/admin/sentences?pageNum=' + nextPage}${keyword ? '&keyword=' + keyword : ''}" aria-controls="example2"  class="page-link">Next</a>
//                   </li>`;
//     $container.append(html);
//
// }

function searchKeyword() {
    const $input = $(".search-sentence");
    if ($input.val().length > 0) {
        window.location.href = `/admin/sentences?keyword=${$input.val().trim()}`;
    }

}