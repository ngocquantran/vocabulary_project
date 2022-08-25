$(function () {
    renderCourseInfo(curCourse);
    renderTopicInfo(topics);
    renderUserCourse(userCourse);
    renderTopicStatus(userTopics);

    courseInfoSlide();
    openChooseWordModalByTopic();
    courseTopicBtn();
    topicProgress();

});


let courseCategory = curCourse.category.id;
const URL_API = "http://localhost:8898/api/v1";


// CSS page-------------------------------------------------------------------

function courseInfoSlide() {
    const slider = $(".course__info-list.lightSlider").lightSlider({
        item: 1,
        loop: true,
        easing: "cubic-bezier(0.25, 0, 0.25, 1)",
        speed: 600,
        pager: false,
        controls: false,
        adaptiveHeight: true,
        responsive: [
            {
                breakpoint: 800,
                settings: {
                    item: 1,
                    slideMove: 1,
                    slideMargin: 6,
                },
            },
            {
                breakpoint: 480,
                settings: {
                    item: 1,
                    slideMove: 1,
                },
            },
        ],
    });
    $(".course__info-next-btn").on("click", function () {
        slider.goToNextSlide();
    });
    switch (courseCategory) {
        case 1:
            $(".learned .course__info-item-progress-item-detail div")
                .eq(1)
                .text("Từ đã thuộc");
            $(".not-learn .course__info-item-progress-item-detail div")
                .eq(1)
                .text("Từ chưa thuộc");
            break;
        case 2:
            $(".learned .course__info-item-progress-item-detail div")
                .eq(1)
                .text("Câu đã thuộc");
            $(".not-learn .course__info-item-progress-item-detail div")
                .eq(1)
                .text("Câu chưa thuộc");
            break;
    }
}

// RENDER PAGE -------------------------------------------------------------------

// Render Course Info


function renderCourseInfo(course) {
    $(".container .course__header").text(course.title);

    course.levels.forEach((level) => {
        let className = level.title.toLowerCase();
        $(`.${className}`).addClass("active");
    });

    let goals = course.goal.split("_");
    renderCourseInfoItem(
        goals,
        $(".course__info-item.goal .course__info-item-content")
    );

    let contents = course.content.split("_");
    renderCourseInfoItem(
        contents,
        $(".course__info-item.content .course__info-item-content")
    );

    let students = course.targetLearner.split("_");
    renderCourseInfoItem(
        students,
        $(".course__info-item.student .course__info-item-content")
    );
}

function renderCourseInfoItem(arr, $container) {
    $container.html("");
    let html = "";
    arr.forEach((element) => {
        html += ` <p>
                    <span
                      >${element}</span
                    >
                  </p>`;
    });
    $container.append(html);
}

// Render Course Topics


function renderTopicInfo(arr) {
    $(".course-content-header span").text(arr.length);
    const $container = $(".course-content-list .row");
    $container.html("");
    let html = "";

    for (let i = 0; i < arr.length; i++) {
        html +=
            ` <div class="col col-lg-3 d-flex justify-content-center">
                  <div class="course-content-item " topic-id="${arr[i].id}">
                 
                    <div class="course-content-item-thumb">
                      <img
                        src="${arr[i].img}"
                        alt=""
                      />
                    </div>

                    <h4 class="course-content-item-name">
                      ${arr[i].title}
                    </h4>

                    <div class="course-content-item-progress">
                      <div class="course-content-item-progress-range">
                        <div class="course-content-item-progress-value"></div>
                        <p><span>0</span>/<span>${
                courseCategory == 1
                    ? arr[i].numberOfVocabs + arr[i].numberOfSens
                    : 15
            }</span></p>
                        <i class="fa-solid fa-star"></i>
                      </div>
                    </div>

                       <div class="course-content-item-btn ${
                i == 0 ? "" : "btn-disabled"
            }">
                          <div class="course-content-item-btn-content">  ` +
            (i == 0
                ? ` <h5>${arr[i].title}</h5>
                        <h6>${
                    courseCategory == 1 ? "từ" : "câu"
                } đã thuộc: <span>0</span>/<span>${
                    arr[i].numberOfVocabs + arr[i].numberOfSens
                }</span></h6>
                        <a href="${
                    courseCategory == 1
                        ? "/filter"
                        : "/learn/sentence"
                }?id=${
                    arr[i].id
                }" class="course-btn-longer">HỌC NGAY</a>`
                :(isNormalUser && isFreeCourse) || !isNormalUser?
                  `  <p>
                              Bài học mới sẽ được mở sau khi bạn hoàn thành bài học
                              hiện tại. Hãy hoàn thành bài học hiện tại theo đúng lộ
                              trình học bạn nhé!
                            </p>`
                : `  <p>Đây là bài học dành cho thành viên PREMIUM, để học bạn cần nâng cấp PREMIUM </p>
                      <a href="/premium" class="course-btn-longer btn-premium">NÂNG CẤP PREMIUM</a>  `) +
            `
                            
                          </div>
                       </div>
                  </div>
                </div>`;
    }

    $container.append(html);
}

// Render topic by User

function renderUserCourse(userCourse) {
    if (userCourse) {
        $(
            ".course__info-item-progress-item.lesson .course__info-item-progress-item-detail div"
        )
            .eq(0)
            .text(userCourse.finishedTopics);
        $(
            ".course__info-item-progress-item.learned .course__info-item-progress-item-detail div"
        )
            .eq(0)
            .text(userCourse.passedVocabs + userCourse.passedSens);
        $(
            ".course__info-item-progress-item.not-learn .course__info-item-progress-item-detail div"
        )
            .eq(0)
            .text(userCourse.failedVocabs + userCourse.failedSens);
    }


}


function renderTopicStatus(userTopicArr) {
    const $topics = $(".course-content-item");

    if (userTopicArr !=null) {
        //Render thông báo theo từng trạng thái của UserTopic: peding, lock, wait, pass
        userTopicArr.forEach((userTopic) => {
            const $topic = $(
                `.course-content-item[topic-id="${userTopic.topic.id}"]`
            );

            $topic
                .find(".course-content-item-progress-range p span")
                .eq(0)
                .text(`${userTopic.passedElement != null ? userTopic.passedElement : 0}`);

            const title = $topic.find(".course-content-item-name").text();
            let html = "";
            switch (userTopic.status) {
                case "PENDING":
                    html = `<div class="course-content-item-btn btn-pending button-active">
                        <div class="course-content-item-btn-content">
                          <h5>${title}</h5>
                          <h6>
                            ${
                        courseCategory == 1
                            ? "Từ đã thuộc"
                            : "Kết quả học"
                    } : <span>0</span> / <span>${userTopic.topic.numberOfVocabs + userTopic.topic.numberOfSens}</span>
                          </h6>
                          <div class="course-btn-longer choose-word" id-topic="${
                        userTopic.topic.id
                    }" data-bs-toggle="modal" data-bs-target="#choosewordmodel"> HỌC TIẾP</div>
                        </div>
                      </div>`;

                    break;
                case "PASS":
                    $topic.addClass("item-active");
                    html = `<div class="course-content-item-btn btn-review">
                        <div class="course-content-item-btn-content">
                          <h5>${title}</h5>
                          <h6>
                            ${
                        courseCategory == 1
                            ? "Từ đã thuộc"
                            : "Kết quả học"
                    } : <span>${
                        userTopic.passedElement
                    }</span> / <span>${userTopic.totalElement}</span>
                          </h6>
                          <a class="course-btn-longer choose-word" id-topic="${
                        userTopic.topic.id
                    }" ${
                        courseCategory == 1
                            ? 'data-bs-toggle="modal" data-bs-target="#choosewordmodel"  href="#"'
                            : `href="/learn/sentence?id=${userTopic.topic.id}"`
                    } > ÔN TẬP LẠI</a>
                          <a href="/result?id=${
                        userTopic.topic.id
                    }" class="course-btn-longer btn-result"
                            >XEM KẾT QUẢ</a
                          >
                          
                        </div>
                      </div>`;
                    break;

                case "CONTINUE":
                    $topic.addClass("item-active");
                    html = `<div class="course-content-item-btn btn-review button-active">
                        <div class="course-content-item-btn-content">
                          <h5>${title}</h5>
                          <h6>
                             ${
                        courseCategory == 1
                            ? "Từ đã thuộc"
                            : "Kết quả học"
                    } : <span>${
                        userTopic.passedElement
                    }</span> / <span>${userTopic.totalElement}</span>
                          </h6>
                          <a class="course-btn-longer choose-word" id-topic="${
                        userTopic.topic.id
                    }" ${
                        courseCategory == 1
                            ? 'data-bs-toggle="modal" data-bs-target="#choosewordmodel"  href="#"'
                            : `href="/learn/sentence?id=${userTopic.topic.id}"`
                    }> ÔN TẬP LẠI</a>
                          <a href="/result?id=${
                        userTopic.topic.id
                    }" class="course-btn-longer btn-result"
                            >XEM KẾT QUẢ</a
                          >
                          
                        </div>
                      </div>`;
                    break;

                case "NOW":
                    if(isNormalUser && !isFreeCourse){
                        html=`<div class="course-content-item-btn btn-disabled button-active">
                          <div class="course-content-item-btn-content">    <p>Đây là bài học dành cho thành viên PREMIUM, để học bạn cần nâng cấp PREMIUM </p>
                      <a href="/premium" class="course-btn-longer btn-premium">NÂNG CẤP PREMIUM</a>  
                            
                          </div>
                       </div>`;
                    }else{
                        $topic.addClass("item-current");
                        html = `<div class="course-content-item-btn button-active">
                        <div class="course-content-item-btn-content">
                          <h5>${title}</h5>
                          <h6>
                             ${
                            courseCategory == 1 ? "Từ" : "Câu"
                        } đã thuộc: <span>0</span> / <span>${courseCategory == 1 ?
                            userTopic.topic.numberOfVocabs : 15
                        }</span> 
                          </h6>
                           <a href="${
                            courseCategory == 1
                                ? "/filter"
                                : "/learn/sentence"
                        }?id=${
                            userTopic.topic.id
                        }" class="course-btn-longer">HỌC NGAY</a>
                          
                        </div>
                      </div>`;
                    }

                    break;
                case "WAITING":
                    html = ` <div class="course-content-item-btn btn-disabled">
                      <div class="course-content-item-btn-content">
                        <p>
                          Bài học mới sẽ được mở sau khi bạn hoàn thành bài học
                          hiện tại. Hãy hoàn thành bài học hiện tại theo đúng lộ
                          trình học bạn nhé!
                        </p>
                      </div>
                    </div>`;
                    break;
                case "LOCKING":
                    html = ` <div class="course-content-item-btn btn-disabled">
                      <div class="course-content-item-btn-content">
                        <p>
                          Bài học này có tính phí, bạn cần mua gói học để tiếp tục.
                        </p>
                      </div>
                    </div>`;
                    break;
            }
            $topic.append(html);
        });
    }
}

function openChooseWordModalByTopic() {
    const $btnOpens = $(".course-btn-longer.choose-word");
    $btnOpens.each(function (index, btn) {
        $(btn).on("click", function () {
            topicId = $(btn).attr("id-topic");
            initChooseWord();
        });
    });
}

// COURSE PAGE ACTION-------------------------------------------
function courseTopicBtn() {
    const topics = $(".course-content-item-thumb");

    $(document).click((event) => {
        if (!$(event.target).closest(".course-content-item-thumb").length) {
            const $curTopicBtn = $(".course-content-item-btn.button-active");
            $curTopicBtn.removeClass("button-active");
        }
    });

    topics.each(function (index, topic) {
        $(topic).on("click", function (e) {
            const $curTopicBtn = $(".course-content-item-btn.button-active");
            $curTopicBtn.removeClass("button-active");
            $(topic).siblings(".course-content-item-btn").addClass("button-active");
        });
    });
}

function topicProgress() {
    const $progressRange = $(".item-active .course-content-item-progress-range");
    $progressRange.each(function (index, progess) {
        const $progressValue = $(progess).find(
            ".course-content-item-progress-value"
        );

        const total = parseInt($(progess).find("span:last-child").text());
        const cur = parseInt($(progess).find("span:first-child").text());
        const $star = $(progess).find("i");
        $progressValue.css({
            width: (cur / total) * 100 + "%",
        });

        if (cur / total >= 0.9) {
            $progressValue.css({
                border: "6px solid #f1b522",
                background: "var(--color-orange)",
            });
            $star.css({
                color: "var(--color-orange)",
            });
        } else if (cur / total > 0) {
            $progressValue.css({
                background: " #8ee000",
                border: "6px solid #7AC70C",
            });
            $star.css({
                color: "#bdbbbb",
            });
        }
    });
}
