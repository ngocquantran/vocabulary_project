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
                  <td>${topic.numberOfVocabs > 0 ? 'Từ vựng' : 'Mẫu câu'}</td>
                  <td>${topic.course ? topic.course.title : 'Chưa gán khóa học'}</td>
         
                  <td>
                    <a href="/admin/topic-detail?id=${topic.id}" type="button" class="btn btn-outline-primary">
                      Xem
                    </a>` +
            (topic.course == null ? `<div  type="button" class="btn btn-outline-danger ms-2" onclick="deleteTopic(${topic.id})">Xóa</div>` : '')

            + `</td>
                </tr>`
    });
    $container.append(html);
}


function searchKeyword() {
    const $input = $(".search-topic");
    if ($input.val().length > 0) {
        window.location.href = `/admin/topics?keyword=${$input.val().trim()}`;
    }

}

function pagination() {
    let $container = $("#pagination");
    $container.pagination({
        dataSource: function (done) {
            var result = [];
            for (var i = 1; i <= pageInfo.totalElements; i++) {
                result.push(i);
            }
            done(result);
        },
        className: 'paginationjs-theme-blue paginationjs-big',
        pageSize: 5,
        pageNumber: pageInfo.currentPage,

    });
    renderPaginationjs();

}

function renderPaginationjs() {
    const $pages = $(".paginationjs-pages>ul>li");
    $pages.each((index, page) => {
        const $link = $(page).find("a");
        const pageNum = $(page).attr("data-num");
        if (!$(page).hasClass("disabled")) {
            $link.on("click", function () {
                window.location.href = `/admin/topics?pageNum=${pageNum}${keyword ? '&keyword=' + keyword : ''}`
            })
        }

    })
}

async function deleteTopic(topicId) {
    try {
        let sure = confirm("Bạn có chắc chắn muốn xóa?");
        if (sure) {
            let res = await axios.delete(`/admin/api/delete-topic?id=${topicId}`);
            console.log("ok rồi");
            window.location.href = "/admin/topics";
            console.log("hihi")
        }


    } catch (e) {
        console.log(e);
    }
}