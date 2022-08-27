package com.example.myvocab.repoTest;

import com.example.myvocab.config.TestJpaConfig;
import com.example.myvocab.model.*;
import com.example.myvocab.repo.CommentsRepo;
import com.example.myvocab.repo.CourseRepo;
import com.example.myvocab.repo.UserCourseRepo;
import com.example.myvocab.request.CommentRequest;
import com.example.myvocab.service.UserLearningService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(
        classes = {TestJpaConfig.class},
        loader = AnnotationConfigContextLoader.class
)
@Transactional
public class CommentRepoTest {
    @Autowired
    private TestEntityManager entityManager;

    @Resource
    private CommentsRepo commentsRepo;

    @BeforeEach
     void create_comments(){
        BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
        Users user1 = Users.builder().email("a@gmail.com").password(passwordEncoder.encode("12345678")).fullName("Trần Văn A").avatar("avatar").build();
        Users user2 = Users.builder().email("b@gmail.com").password(passwordEncoder.encode("12345678")).fullName("Trần Văn B").avatar("avatar").build();

        CourseGroup group1 = CourseGroup.builder().title("Khóa học miễn phí").build();
        CourseCategory category1 = CourseCategory.builder().title("từ vựng").img("").build();
        Levels level1 = Levels.builder().title("A0").build();

        Course course = Course.builder().title("Let go").category(category1).group(group1).levels(List.of(level1)).build();
        entityManager.persist(course);

        UserCourse uc1=entityManager.persist(UserCourse.builder().user(user1).course(course).build());
        UserCourse uc2=entityManager.persist(UserCourse.builder().user(user2).course(course).build());

        Topic topic = Topic.builder().title("JOB").img("").course(course).build();
        entityManager.persist(topic);
        UserTopic ut1=UserTopic.builder().topic(topic).userCourse(uc1).build();
        UserTopic ut2=UserTopic.builder().topic(topic).userCourse(uc2).build();
        entityManager.persist(ut1);
        entityManager.persist(ut2);
        entityManager.flush();


        Comments cm1=Comments.builder()
                .idParent(null)
                .createdAt(LocalDateTime.now().minusMinutes(3))
                .userTopic(ut1)
                .message("haha").build();
        Comments cm2=Comments.builder()
                .idParent(null)
                .createdAt(LocalDateTime.now().minusMinutes(2))
                .userTopic(ut2)
                .message("hehe").build();
        Comments cm3=Comments.builder()
                .idParent(1L)
                .createdAt(LocalDateTime.now().minusMinutes(1))
                .userTopic(ut1)
                .message("huhu").build();

        entityManager.persist(cm1);
        entityManager.persist(cm2);
        entityManager.persist(cm3);
        entityManager.flush();

    }

    @Test
    void find_comment_by_id_and_topic_id(){
        assertThat(commentsRepo.findByIdAndUserTopic_Topic_Id(4L,1L)).isEmpty();
        assertThat(commentsRepo.findByIdAndUserTopic_Topic_Id(2L,1L)).isPresent();
        assertThat(commentsRepo.findByIdAndUserTopic_Topic_Id(3L,2L)).isEmpty();

        assertThat(commentsRepo.findByUserTopic_Topic_Id(1L)).hasSize(3);
        assertThat(commentsRepo.findByUserTopic_Topic_Id(2L)).hasSize(0);
    }





}
