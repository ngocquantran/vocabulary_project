package com.example.myvocab.serviceTest;


import com.example.myvocab.config.TestJpaConfig;
import com.example.myvocab.dto.VocabTestDto;
import com.example.myvocab.exception.NotFoundException;
import com.example.myvocab.model.*;
import com.example.myvocab.model.enummodel.LearningStage;
import com.example.myvocab.model.enummodel.TopicState;
import com.example.myvocab.repo.*;
import com.example.myvocab.request.FilterVocabRequest;
import com.example.myvocab.request.LearnVocabRequest;
import com.example.myvocab.service.UserLearningService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

import static org.mockito.Mockito.*;

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

    @MockBean
    private VocabRepo vocabRepo;

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

    @Test
    void initUserTopicVocabs() {
        String userId = user.getId();
        Long topicId = topic.getId();
        Mockito.when(userTopicRepo.findByTopic_IdAndUserCourse_User_Id(topicId, userId, UserTopic.class)).thenReturn(Optional.of(userTopic));
        assertThat(topic.getVocabs()).hasSize(2);

        Mockito.when(userTopicVocabRepo.findByUserTopic_IdAndVocab_Id(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.empty());

        ArgumentCaptor<UserTopicVocab> captor = ArgumentCaptor.forClass(UserTopicVocab.class);

        service.initUserTopicVocabs(topicId, userId);

        verify(userTopicVocabRepo, times(2)).save(captor.capture());
        verify(userTopicVocabRepo, times(2)).save(Mockito.any(UserTopicVocab.class));
        assertThat(captor.getAllValues().get(0).getVocab().getWord()).isEqualTo("book");
        assertThat(captor.getAllValues().get(1).getVocab().getWord()).isEqualTo("love");
    }

    @Test
    void updateUserTopicRecordFromFilterRequest() {
        FilterVocabRequest r1 = FilterVocabRequest.builder().vocabId(1L).status(false).build();
        FilterVocabRequest r2 = FilterVocabRequest.builder().vocabId(2L).status(true).build();
        List<FilterVocabRequest> requests = List.of(r1, r2);

        service.updateUserTopicRecordFromFilterRequest(userTopicRecord, requests);
        assertThat(userTopicRecord.getRightAnswers()).isEqualTo(1);
        assertThat(userTopicRecord.getTotalAnswers()).isEqualTo(2);
    }

    @Test
    void updateUserTopicVocabFromFilterRequest() {
        List<UserTopicVocab> list = new ArrayList<>();
        for (Vocab v : topic.getVocabs()) {
            UserTopicVocab userTopicVocab = UserTopicVocab.builder()
                    .userTopic(userTopic)
                    .vocab(v)
                    .status(false)
                    .build();
            list.add(userTopicVocab);
        }

        FilterVocabRequest r1 = FilterVocabRequest.builder().vocabId(1L).status(false).build();
        FilterVocabRequest r2 = FilterVocabRequest.builder().vocabId(2L).status(true).build();
        List<FilterVocabRequest> requests = List.of(r1, r2);

        Mockito.when(userTopicVocabRepo.findByUserTopic_IdAndVocab_Id(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(list.get(0)), Optional.of(list.get(1)));
        ArgumentCaptor<UserTopicVocab> captor = ArgumentCaptor.forClass(UserTopicVocab.class);

        service.updateUserTopicVocabFromFilterRequest(userTopic, requests);
        verify(userTopicVocabRepo, times(2)).save(captor.capture());
        assertThat(captor.getAllValues().get(0).isStatus()).isFalse();
        assertThat(captor.getAllValues().get(1).isStatus()).isTrue();

    }

    @Test
    void updateUserTopicVocabFromLearnRequest(){
        List<UserTopicVocab> list = new ArrayList<>();
        for (Vocab v : topic.getVocabs()) {
            UserTopicVocab userTopicVocab = UserTopicVocab.builder()
                    .userTopic(userTopic)
                    .vocab(v)
                    .status(true)
                    .build();
            list.add(userTopicVocab);
        }
        LearnVocabRequest r1=LearnVocabRequest.builder().vocabId(1L).learn(false).build();
        LearnVocabRequest r2=LearnVocabRequest.builder().vocabId(2L).learn(false).build();
        List<LearnVocabRequest> requests=List.of(r1,r2);

        Mockito.when(userTopicVocabRepo.findByUserTopic_IdAndVocab_Id(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Optional.of(list.get(0)), Optional.of(list.get(1)));
        ArgumentCaptor<UserTopicVocab> captor = ArgumentCaptor.forClass(UserTopicVocab.class);

        service.updateUserTopicVocabFromLearnRequest(userTopic, requests);
        verify(userTopicVocabRepo, times(2)).save(captor.capture());
        assertThat(captor.getAllValues().get(0).isLearn()).isFalse();
        assertThat(captor.getAllValues().get(1).isLearn()).isFalse();
    }

    @Test
    void renderVocabAnswers(){
        VocabTestDto vocabTest=VocabTestDto.builder().id(1L).word("book").answerIndex(2).vocabs(new ArrayList<>()).build();

        Vocab v1 = Vocab.builder().id(1L).word("flower").build();
        Vocab v2 = Vocab.builder().id(2L).word("bag").build();
        Vocab v3 = Vocab.builder().id(3L).word("book").build();
        Vocab v4 = Vocab.builder().id(4L).word("love").build();
        Vocab v5= Vocab.builder().id(5L).word("bank").build();
        Vocab v6= Vocab.builder().id(6L).word("barber").build();
        Mockito.when(vocabRepo.findByTopics_Course_Id(Mockito.anyLong())).thenReturn(List.of(v1,v2,v3,v4,v5,v6));

        VocabTestDto testDto=service.renderVocabAnswers(vocabTest,1L);

        assertThat(testDto.getVocabs()).hasSize(4);
        assertThat(testDto.getVocabs().get(1)).isEqualTo("book");
        assertThat(testDto.getVocabs()).doesNotHaveDuplicates();

    }

    @Test
    void renderEnSentenceAnswers(){
        VocabTestDto vocabTest=VocabTestDto.builder().id(1L).enSentence("book").answerIndex(4).enSentences(new ArrayList<>()).build();

        Vocab v1 = Vocab.builder().id(1L).enSentence("flower").build();
        Vocab v2 = Vocab.builder().id(2L).enSentence("bag").build();
        Vocab v3 = Vocab.builder().id(3L).enSentence("book").build();
        Vocab v4 = Vocab.builder().id(4L).enSentence("love").build();
        Vocab v5= Vocab.builder().id(5L).enSentence("bank").build();
        Vocab v6= Vocab.builder().id(6L).enSentence("barber").build();
        Mockito.when(vocabRepo.findByTopics_Course_Id(Mockito.anyLong())).thenReturn(List.of(v1,v2,v3,v4,v5,v6));

        VocabTestDto testDto=service.renderEnSentenceAnswers(vocabTest,1L);

        assertThat(testDto.getEnSentences()).hasSize(4);
        assertThat(testDto.getEnSentences().get(3)).isEqualTo("book");
        assertThat(testDto.getEnSentences()).doesNotHaveDuplicates();
    }






}
