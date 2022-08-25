$(function (){
renderCourseInfo();
$(".save-course").on("click",generateData)
})

function renderCourseInfo(){

    $(".title textarea").val(course.title);
    $(".description textarea").val(course.description);

    //course status
    const $statusOptions=$(".status option");
    $statusOptions.each((index,status)=>{
        if ($(status).attr("value")==course.status){
            $(status).prop("selected",true);
        }
    })

    //course level
    const $levelContainer=$(".level select");
    let levelHtml=` <option value="1" ${course.levelTitles.includes("A0")?"selected":""}>A0</option>
                        <option value="2"  ${course.levelTitles.includes("A1")?"selected":""}>A1</option>
                        <option value="3"  ${course.levelTitles.includes("A2")?"selected":""}>A2</option>
                        <option value="4"  ${course.levelTitles.includes("B1")?"selected":""}>B1</option>
                        <option value="5"  ${course.levelTitles.includes("B2")?"selected":""}>B2</option>
                        <option value="6"  ${course.levelTitles.includes("C1")?"selected":""}>C1</option>
                        <option value="7"  ${course.levelTitles.includes("C2")?"selected":""}>C2</option>`;
    $levelContainer.append(levelHtml);

    //course group
    const $groupContainer=$(".group select");
    let groupHtml="";
    groups.forEach(group=>{
        groupHtml+=`<option value="${group.id}"  ${group.id==course.group.id?"selected":""}>${group.title}</option>`
    });
    $groupContainer.append(groupHtml);


    //target

    $(".course-target").val(course.goal.replaceAll("_","\n"));
    $(".course-content").val(course.content.replaceAll("_","\n"));
    $(".course-student").val(course.targetLearner.replaceAll("_","\n"));

    //course topic
    // const $topicContainer=$(".select-topics");
    // let topicHtml="";
    // topics.forEach(topic=>{
    //     topicHtml+=`<option value="${topic.id}" selected>${topic.title}</option>`
    // })
    // $topicContainer.append(topicHtml);


}

function generateData(){
    let levels=$(".select-level").select2("data").map(d=>d.id);
    console.log(levels);
    let status=$(".status select").val();
    console.log(status);
    let group=$(".group select").val();
    console.log(group);
    let title=escapeHtml($(".title textarea").val().trim()) ;
    console.log(title);
    let description=escapeHtml($(".description textarea").val().trim());
    console.log(description);
    let goal=escapeHtml($(".target textarea").val().trim()).replaceAll("\n","_");
    console.log(goal);
    let content=escapeHtml($(".course-content").val().trim()).replaceAll("\n","_");
    console.log(content);
    let student=escapeHtml($(".student textarea").val().trim()).replaceAll("\n","_");
    console.log(student);

    if(levels.length==0||title.length==0||description.length==0||goal.length==0||content.length==0||student.length==0){
        alert("Bạn cần nhập đầy đủ thông tin");
        return;
    }else {
        let request={
            title,
            description,
            goal,
            content,
            targetLearner:student,
            status,
            groupId:group,
            categoryId:course.category.id,
            levels
        };

        postEditRequest(request);
    }

}
async function postEditRequest(request){
    try {
        let res=await axios.post(`/admin/api/edit-course?id=${course.id}`,request);
        console.log("ok rồi");
        window.location.href=`/admin/course-detail?id=${course.id}`;
    }catch (e){
        console.log(e);
    }

}


let entityMap={
    '<':'&lt;',
    '>':'&gt;',
    '&':'&amp',
    '/':'&frasl;',
    '=':'&equals;'
}

function escapeHtml(string){
    return String(string).replace(/[<>&/=]/g,function (s) {
        return entityMap[s];
    })
}


