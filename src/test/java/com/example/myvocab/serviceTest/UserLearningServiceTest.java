package com.example.myvocab.serviceTest;


import com.example.myvocab.config.TestJpaConfig;
import com.example.myvocab.model.Course;
import com.example.myvocab.model.UserCourse;
import com.example.myvocab.model.Users;
import com.example.myvocab.repo.CommentsRepo;
import com.example.myvocab.repo.UserCourseRepo;
import com.example.myvocab.repo.UsersRepo;
import com.example.myvocab.service.UserLearningService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.transaction.Transactional;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class UserLearningServiceTest {

    @MockBean
    private UserCourseRepo userCourseRepo;
    @MockBean
    private UsersRepo usersRepo;



    @InjectMocks
    private UserLearningService service;

    @Test
    void  create_user_course(){
        String userId="1";
        Users user=Users.builder().id("1").fullName("user1").build();

        Course course=Course.builder().id(1L).title("course1").build();
        Mockito.when(userCourseRepo.findByCourse_IdAndUser_Id(course.getId(),userId)).thenReturn(Optional.empty());
        UserCourse userCourse=service.createUserCourse(course,user);

        assertThat(userCourse).isNull();
    }








}
