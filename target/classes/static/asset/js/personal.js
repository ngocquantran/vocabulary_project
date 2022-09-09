$(function () {
    renderUserInfo();
})

function renderUserInfo() {
    $(".person-info-content .avatar img").attr("src", user.avatar);
    $(".person-info-content> h5").text(user.fullName);
    const userRoles = user.userRoles.map((u) => {
        return u.role.name;
    })
    if (userRoles.includes("USER_VIP")) {
        $(".person-info-content>.status").text("Thành viên VIP");
        $(".person-info-content>.upgrade").css({display: "none"});
    } else {
        $(".person-info-content>.status").text("Thành viên thường");
        $(".person-info-content>.upgrade").css({display: "block"});
    }
    const date = new Date(user.startDate);
    $(".person-info-content>.start span").text(`${date.getMonth()}, ${date.getFullYear()}`);

    $(".study-content .item-course > strong").text(userCourses.length);
    $(".study-content .item-topic > strong").text(numberOfTopics);

    $(".cate-content .item-vocab .cate-progress > span").text(userCourses.filter(c => c.course.category.id === 1).length);
    $(".cate-content .item-sentence .cate-progress > span").text(userCourses.filter(c => c.course.category.id === 2).length);

    let html = "";
    $(".learn-content").html("");
    userCourses.forEach(c => {
        let cDate = new Date(c.studiedAt);
        console.log(cDate);

        html += ` <div class="learn-item ${c.course.category.id === 1 ? 'item-vocab' : 'item-sen'}">
          <a href="/course?id=${c.course.id}">
            <div class="learn-date">${cDate.getDate()}/${cDate.getMonth()+1}/${cDate.getFullYear()}</div>
            <div class="learn-thumb">
              <img
                      src="${c.course.thumbnail}"
                      alt=""
              />
            </div>
            <div class="learn-text">
              <div class="title">
                ${c.course.title}
              </div>
              <div class="cate">${c.course.category.id === 1 ? 'Từ vựng' : 'Mẫu câu'}</div>
            </div>
            <div class="learn-progress">
              <div class="learn-progress-value" style="display: none">
                <span>${c.finishedTopics>0?c.finisedTopics:0}</span><span>${c.course.numberOfTopics}</span>
              </div>
              <div class="progress-bar">
                <div class="progress-bar-value" style="width: ${c.finishedTopics?100*c.finishedTopics/c.course.numberOfTopics:0}%"></div>
              </div>
            </div>
          </a>
        </div>`;
    })
    $(".learn-content").append(html);

}

