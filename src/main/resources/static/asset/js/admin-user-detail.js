$(function (){
renderPersonalInfo();
renderUserCourse();
renderPackage();
});

function renderPersonalInfo(){
    const $container=$(".person-info-content");
    $container.html("");
    let html=` <div class="avatar-img">
                    <img
                            src="${user.avatar}"
                            alt="Quân"
                    />
                  </div>

                  <h5>${user.fullName}</h5>

                  <div class="status">${user.userRoles.map(r=>r.role.name).includes("USER_NORMAL")?'Thành viên thường':'Thành viên VIP'}</div>

                  <div class="start">
                    Đã tham gia <span>${user.startDate}</span>
                  </div>
                  <div class="email">${user.email}</div>
                  <div class="phone">${user.phone?user.phone:''}</div>`;
    $container.append(html);
}

function renderUserCourse(){
    const $container=$(".table-study-content");
    $container.html("");
    let html="";
    userCourses.forEach((course,index)=>{
        html+=` <tr class="text-center">
                    <td>${index+1}</td>
                    <td>${course.course.title}</td>
                    <td>${course.course.category.title}</td>
                    <td>${course.finishedTopics} / ${course.course.numberOfTopics} chủ đề</td>
                  </tr>`
    });
    $container.append(html);
}

function renderPackage(){
    const $container=$(".table-package-content");
    $container.html("");
    let html="";
    activeOrders.forEach((order,index)=>{
        let orderDate=new Date(order.orderDate);
        let activeDate = new Date(order.activeDate);
        let expiredDate=new Date();
        expiredDate.setDate(activeDate.getDate()+parseInt(order.aPackage.duration)*30);

        html+=`<tr class="text-center">
                    <td>${order.aPackage.title}</td>
                    <td>${order.aPackage.duration} tháng</td>
                    <td>${orderDate.getDate()}/${orderDate.getMonth()+1}/${orderDate.getFullYear()}</td>
                    <td>${activeDate.getDate()}/${activeDate.getMonth()+1}/${activeDate.getFullYear()}</td>
                    <td>${expiredDate.getDate()}/${expiredDate.getMonth()+1}/${expiredDate.getFullYear()}</td>
                   
                  </tr>`
    });
    $container.append(html);
}