$(function () {
    renderUserCourses();
    renderPageByCategory();



});


function renderPageByCategory() {
    renderGroupHtml(originalGroupsCourses);
    filterByGroup();
    filterByLevels();
    filterBySearchInput();

}


// Render khóa học theo nhóm từ---------------------------------------------------------
function renderGroupHtml(groupArr) {
    const $container = $(".container__courses-content");
    $container.html("");
    let html = "";
    groupArr.forEach(group => {
        const courseHtml = renderCourseHtml(group.courses, group);
        html += `<div class="container__courses-group" id-group="${group.id}">
              <div class="container__courses-group-header">
                <div class="container__courses-group-header-line"></div>
                <div class="container__courses-group-header-name">${group.title}</div>
              </div>

              <div class="container__courses-group-wrapper">
                <div class="row row-cols-lg-5 row-cols-md-3 row-cols-2">` + courseHtml + `
              </div>
            </div></div>`
    });
    $container.append(html);

}

function renderCourseHtml(courseArr, group) {
    let html = "";
    courseArr.forEach(course => {
        const levelArr = course.levels.map(level => level.title);
        html += `<div class="col">
              <div class="container__courses-group-item" id-course="${course.id}" id-group="${group.id}">
                <a href="/course?id=${course.id}">
                  <img src="${course.thumbnail}" alt="">
                  <h4>${course.title}</h4>
                  <div class="container__courses-group-item-lesson">
                    <span>${course.numberOfTopics}</span> lessons
                  </div>
                  <div class="container__courses-group-item-level">
                    <span class="a0 ${levelArr.includes("A0") ? 'active' : ''}">A0</span>
                    <span class="a1 ${levelArr.includes("A1") ? 'active' : ''}">A1</span>
                    <span class="a2 ${levelArr.includes("A2") ? 'active' : ''}">A2</span>
                    <span class="b1 ${levelArr.includes("B1") ? 'active' : ''}">B1</span>
                    <span class="b2 ${levelArr.includes("B2") ? 'active' : ''}">B2</span>
                    <span class="c1 ${levelArr.includes("C1") ? 'active' : ''}">C1</span>
                    <span class="c2 ${levelArr.includes("C2") ? 'active' : ''}">C2</span>
                  </div>
                  <div class="container__courses-group-item-desc"> ${course.description}</div>
                </a>
              </div>
            </div>`
    })
    return html;
}


//  Lọc Khóa học theo Nhóm
function filterByGroup() {
    const $groupItems = $(".filter-group.dropdown ul .dropdown-item");
    $groupItems.each(function (index, item) {
        $(item).on("click", function () {
            $(".filter-search input").val("");

            let itemId = $(this).attr("id-group");

            if (itemId == 0) {
                renderGroupHtml(originalGroupsCourses);
            } else {
                let filteredGroups = originalGroupsCourses.filter((group) => {
                    return group.id == itemId;
                });
                renderGroupHtml(filteredGroups);
            }
        });
    });
}

// Lọc khóa học theo level
function filterByLevels() {
    const $levelItems = $(".filter-level.dropdown ul .dropdown-item");
    $levelItems.each(function (index, item) {
        $(item).on("click", function () {
            $(".filter-search input").val("");

            let levelId = parseInt($(this).attr("id-level"));
            let filteredArr = originalGroupsCourses.reduce((acc, curr) => {
                let courses = curr.courses.filter((course) => {
                    let itemLevels = course.levels.map((level) => {
                        return level.id;
                    });
                    return itemLevels.includes(levelId);
                });
                if (courses.length === 0) {
                    return acc;
                }
                acc.push({...curr, courses: courses});
                return acc;
            }, [])
            console.log(filteredArr);
            renderGroupHtml(filteredArr)
        });
    });
}

function renderUserCourses(){
    if(!userCourses || userCourses.length==0){
        $(".container__cur-course").hide();
    }else {
        $(".container__cur-course").show();
        $(".container__section-header").text("Khóa bạn đang học");
        const $container=$(".container__cur-course-list");
        $container.html("");
        let html="";
        userCourses.forEach(course=>{
            html+=`<li class="container__cur-course-item">
                        <a href="/course?id=${course.course.id}">
                            <div class="container__cur-course-item-thumb">
                                <img alt="" src="${course.course.thumbnail}">
                            </div>
                            <div class="container__cur-course-item-detail">
                                <div class="container__cur-course-item-name">
                                    ${course.course.title}
                                </div>
                                <div class="container__cur-course-item-progress">
                                    <div class="container__cur-course-item-progress-desc">
                                        Đã học <span>${course.finishedTopics}</span> / <span>${course.course.numberOfTopics}</span> bài
                                    </div>
                                    <div class="container__cur-course-item-progress-bar">
                                        <div class="container__cur-course-item-progress-value" style="width: ${course.finishedTopics?100*course.finishedTopics/course.course.numberOfTopics:0}%"></div>
                                    </div>
                                </div>
                            </div>
                        </a>
                    </li>`
        });
        $container.append(html);
    }
}

function filterBySearchInput(){
    const $input = $(".filter-search input");
    $input.on("keyup", function (e) {
        let keySearch=$(this).val();
        console.log(keySearch);
        let filteredArr = originalGroupsCourses.reduce((acc, curr) => {
            let courses = curr.courses.filter((course) => {
                return course.title.toLowerCase().includes(keySearch.toLowerCase()) || course.description.toLowerCase().includes(keySearch.toLowerCase());
            });
            if (courses.length === 0) {
                return acc;
            }
            acc.push({...curr, courses: courses});
            return acc;
        }, [])
        console.log(filteredArr);
        renderGroupHtml(filteredArr)
    });
}

