$(function () {
    renderSelection();
    chooseImage();
    $(".category select").change(function () {
        let cateId = parseInt($(this).val());
        getGroupByCategoryId(cateId);
        if (cateId == 1) {
            renderTopicsToChoose("Từ vựng");
        } else {
            renderTopicsToChoose("Mẫu câu");
        }
    });
    $(".save-course").on("click", generateData);


})


function chooseImage() {
    const $imageInput = $(".update-photo input");
    $imageInput.change(function () {
        console.log($imageInput.val());
        const reader = new FileReader();
        reader.addEventListener("load", () => {
            let uploaded_image = reader.result;
            $(".course-photo .photo img").attr("src", `${uploaded_image}`);
        });
        reader.readAsDataURL(this.files[0]);
    });
}

function renderSelection() {

    //category
    const $categoryContainer = $(".category select");
    $categoryContainer.html("");
    let cateHtml = "";
    categories.forEach(cate => {
        cateHtml += `<option value="${cate.id}">${cate.title}</option>`
    });
    $categoryContainer.append(cateHtml);
    let cateId = parseInt($categoryContainer.val());
    console.log(cateId);
    getGroupByCategoryId(cateId);
    if (cateId == 1) {
        renderTopicsToChoose("Từ vựng");
    } else {
        renderTopicsToChoose("Mẫu câu");
    }


    //levels
    const $levelsContainer = $(".select-level");
    $levelsContainer.html("");
    let levelHtml = "";
    levels.forEach(level => {
        levelHtml += `<option value="${level.id}">${level.title}</option>`
    });
    $levelsContainer.append(levelHtml);

}

function renderGroupByCategory(arr) {
    const $container = $(".group select");
    $container.html("");
    let html = "";
    arr.forEach(group => {
        html += `<option value="${group.id}">${group.title}</option>`
    });
    $container.append(html);
}

async function getGroupByCategoryId(categoryId) {
    try {
        let res = await axios.get(`/admin/api/groups?id=${categoryId}`);
        console.log(res);
        renderGroupByCategory(res.data);
    } catch (e) {
        console.log(e);
    }
}

function renderTopicsToChoose(category) {
    const $container = $(".select-topics");
    $container.html("");
    let html = "";
    let topicToRender;
    if (category == "Từ vựng") {
        topicToRender = topics.filter(t => t.numberOfVocabs > 0);
    } else {
        topicToRender = topics.filter(t => t.numberOfSens > 0);
    }
    console.log(topicToRender);
    topicToRender.forEach(topic => {
        html += `<option value="${topic.id}">${topic.title}</option>`
    });
    $container.append(html);
}


function generateData() {
    let img=$(".update-photo input").prop("files")[0];
    console.log(img);
    let levels = $(".select-level").select2("data").map(d => d.id);
    console.log(levels);
    let chosedTopics = $(".select-topics").select2("data").map(d => d.id);
    console.log(chosedTopics);
    let status = $(".status select").val();
    console.log(status);
    let category = $(".category select").val();
    console.log(category);
    let group = $(".group select").val();
    console.log(group);
    let title = escapeHtml($(".title textarea").val().trim());
    console.log(title);
    let description = escapeHtml($(".description textarea").val().trim());
    console.log(description);
    let goal = escapeHtml($(".target textarea").val().trim()).replaceAll("\n", "_");
    console.log(goal);
    let content = escapeHtml($(".course-content").val().trim()).replaceAll("\n", "_");
    console.log(content);
    let student = escapeHtml($(".student textarea").val().trim()).replaceAll("\n", "_");
    console.log(student);

    if (img.length==0|| chosedTopics.length==0||levels.length == 0 || title.length == 0 || description.length == 0 || goal.length == 0 || content.length == 0 || student.length == 0) {
        alert("Bạn cần nhập đầy đủ thông tin");

    } else {
        let request = {
            title,
            status,
            categoryId: category,
            groupId: group,
            levels,
            description,
            goal,
            content,
            targetLearner: student,
            topics:chosedTopics
        };
        const formData = new FormData();
        formData.append("course",new Blob([JSON.stringify(request)], {
            type: "application/json"
        }));
        formData.append("img",img);
         postAddRequest(formData);
    }

}

async function postAddRequest(formData) {
    try {
        let res = await axios.post(`/admin/api/add-course`,formData, {
            headers: {
                Accept: 'application/json',
                "Content-Type": "multipart/form-data;",
            },
        });
        console.log("ok rồi");
        window.location.href = `/admin/courses`;
    } catch (e) {
        console.log(e);
    }

}


let entityMap = {
    '<': '&lt;',
    '>': '&gt;',
    '&': '&amp',
    '/': '&frasl;',
    '=': '&equals;'
}

function escapeHtml(string) {
    return String(string).replace(/[<>&/=]/g, function (s) {
        return entityMap[s];
    })
}