$(function () {
    renderCourseInfo(course);
    renderTopicInfo(topics);
    $(".delete-course-btn").on("click", deleteCourse);


    courseInfoSlide();
});


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

                   
                  </div>
                </div>`;
    }

    $container.append(html);
}

async function deleteCourse() {
    try {
        let sure = confirm("Bạn có chắc chắn muốn xóa");
        if (sure) {
            let res = await axios.delete(`/admin/api/delete-course?id=${course.id}`);
            console.log("ok rồi");
            window.location.href = "/admin/courses";
        }

    } catch (e) {
        console.log(e);
    }
}







