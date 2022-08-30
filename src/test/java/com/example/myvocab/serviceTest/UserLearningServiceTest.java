package com.example.myvocab.serviceTest;


import com.example.myvocab.config.TestJpaConfig;
import com.example.myvocab.exception.NotFoundException;
import com.example.myvocab.model.*;
import com.example.myvocab.model.enummodel.LearningStage;
import com.example.myvocab.model.enummodel.TopicState;
import com.example.myvocab.repo.*;
import com.example.myvocab.request.FilterVocabRequest;
import com.example.myvocab.service.UserLearningService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class UserLearningServiceTest {
    @InjectMocks
    private UserLearningService service;

    @MockBean
    private UserCourseRepo userCourseRepo;

    @MockBean
    private UserTopicRepo userTopicRepo;

    @MockBean
    private UserTopicRecordRepo userTopicRecordRepo;

    @MockBean
    private UserTopicVocabRepo userTopicVocabRepo;

    private Users user;
    private Course course;
    private Topic topic;
    private UserCourse userCourse;
    private UserTopic userTopic;
    private UserTopicRecord userTopicRecord;


    @BeforeEach
    void initData() {
        course = Course.builder().id(1L).title("khóa học 1").build();
        topic = Topic.builder().id(1L).title("chủ đề 1").course(course).vocabs(new ArrayList<>()).build();
        user = Users.builder().id("1").fullName("người dùng 1").build();
        userCourse = UserCourse.builder().id(1L).user(user).course(course).build();
        userTopic = UserTopic.builder()
                .id(1L)
                .status(TopicState.NOW)
                .topic(topic)
                .userCourse(userCourse)
                .build();
        userTopicRecord = UserTopicRecord.builder()
                .testTime(0)
                .rightAnswers(0)
                .totalAnswers(0)
                .stage(LearningStage.FIRST)
                .userTopic(userTopic)
                .build();
        Vocab v1 = Vocab.builder().id(1L).word("book").topics(new ArrayList<>()).build();
        Vocab v2 = Vocab.builder().id(2L).word("love").topics(new ArrayList<>()).build();
        v1.addTopic(topic);
        v2.addTopic(topic);
        topic.addVocab(v1);
        topic.addVocab(v2);

    }


    @Test
    void create_userCourse() {
        String userId = user.getId();

        Mockito.when(userCourseRepo.findByCourse_IdAndUser_Id(course.getId(), userId)).thenReturn(Optional.empty());

        Mockito.when(userCourseRepo.save(Mockito.any(UserCourse.class))).thenReturn(userCourse);

        assertThat(service.createUserCourse(course, user)).isEqualTo(userCourse);
    }

    @Test
    void create_pending_userTopic_first_time() {
        String userId = user.getId();

        Mockito.when(userCourseRepo.findByCourse_IdAndUser_Id(course.getId(), userId)).thenReturn(Optional.empty());
        Mockito.when(userCourseRepo.save(Mockito.any(UserCourse.class))).thenReturn(userCourse);

        Mockito.when(userTopicRepo.findByTopic_IdAndUserCourse_User_Id(topic.getId(), user.getId(), UserTopic.class)).thenReturn(Optional.empty());

        userTopic.setStatus(TopicState.PENDING);
        Mockito.when(userTopicRepo.save(Mockito.any(UserTopic.class))).thenReturn(userTopic);

        assertThat(service.createPendingUserTopic(topic, user)).isEqualTo(userTopic);
    }

    @Test
    void create_pending_userTopic_when_already_exist() {
        String userId = user.getId();

        Mockito.when(userCourseRepo.findByCourse_IdAndUser_Id(course.getId(), userId)).thenReturn(Optional.empty());

        Mockito.when(userCourseRepo.save(Mockito.any(UserCourse.class))).thenReturn(userCourse);

        userTopic.setStatus(TopicState.NOW);
        Mockito.when(userTopicRepo.findByTopic_IdAndUserCourse_User_Id(topic.getId(), user.getId(), UserTopic.class)).thenReturn(Optional.of(userTopic));
        Mockito.when(userTopicRepo.save(Mockito.any(UserTopic.class))).thenReturn(userTopic);

        assertThat(service.createPendingUserTopic(topic, user).getStatus()).isEqualTo(TopicState.PENDING);
    }

    @Test
    void create_userTopicRecord_by_stage() {
        String userId = user.getId();
        Long topicId = topic.getId();
        userTopicRecord.setStage(LearningStage.NOW);

        Mockito.when(userTopicRepo.findByTopic_IdAndUserCourse_User_Id(topicId, userId, UserTopic.class)).thenReturn(Optional.of(userTopic));
        LearningStage stage = LearningStage.NOW;
        Mockito.when(userTopicRecordRepo.findByStageAndUserTopic_Id(stage, userTopic.getId())).thenReturn(Optional.empty());

        Mockito.when(userTopicRecordRepo.save(Mockito.any(UserTopicRecord.class))).thenReturn(userTopicRecord);

        assertThat(service.createUserTopicRecordByStage(topicId, userId, stage)).isEqualTo(userTopicRecord);

    }

//    @Test
//    void initUserTopicVocabs(){
//        String userId = user.getId();
//        Long topicId = topic.getId();
//        Mockito.when(userTopicRepo.findByTopic_IdAndUserCourse_User_Id(topicId, userId, UserTopic.class)).thenReturn(Optional.of(userTopic));
//        assertThat(topic.getVocabs()).hasSize(2);
//
//        List<UserTopicVocab> list=mock(List.class);
//
//        Vocab v=topic.getVocabs().get(0);
//        Mockito.when(userTopicVocabRepo.findByUserTopic_IdAndVocab_Id(userTopic.getId(), v.getId())).thenReturn(Optional.empty());
//        UserTopicVocab userTopicVocab = UserTopicVocab.builder()
//                      .userTopic(userTopic)
//                       .vocab(v)
//                       .build();
//      list.add(Mockito.when(userTopicVocabRepo.save(Mockito.any(UserTopicVocab.class))).thenReturn(userTopicVocab).getMock()) ;
//       service.initUserTopicVocabs(topicId,userId);
//       assertThat(list).hasSize(2);
//    }







}
