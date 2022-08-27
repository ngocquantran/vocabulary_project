package com.example.myvocab.repoTest;


import com.example.myvocab.config.TestJpaConfig;
import com.example.myvocab.dto.TopicHaveCourseDto;
import com.example.myvocab.model.*;
import com.example.myvocab.repo.CourseRepo;
import com.example.myvocab.repo.TopicRepo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.annotation.Resource;
import javax.persistence.Entity;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@ContextConfiguration(
        classes = {TestJpaConfig.class},
        loader = AnnotationConfigContextLoader.class
)
@Transactional
public class TopicRepoTest {

    @Autowired
    private TestEntityManager entityManager;

    @Resource
    private TopicRepo topicRepo;

    @BeforeEach
    void create_topic() {
        CourseGroup group1 = CourseGroup.builder().title("Khóa học miễn phí").build();
        CourseCategory category1 = CourseCategory.builder().title("từ vựng").img("").build();
        Levels level1 = Levels.builder().title("A0").build();

        Course course = Course.builder()
                .title("Let go")
                .category(category1)
                .group(group1)
                .levels(List.of(level1))
                .build();

        Topic topic1 = Topic.builder()
                .title("JOB")
                .img("")
                .course(course)
                .build();

        Topic topic2 = Topic.builder()
                .title("FAMILY")
                .img("")
                .build();

        entityManager.persist(topic1);
        entityManager.persist(topic2);
        entityManager.flush();

    }


//    @Test
//    void find_topic_by_course_id() {
//        List<Topic> topics = topicRepo.findByCourse_IdOrderByIdAsc(1L);
//        assertThat(topics).hasSize(1);
//    }


    @Test
    void find_topic_by_keyword() {

        Pageable pageable = PageRequest.of(0, 5);
        Page<TopicHaveCourseDto> page1 = topicRepo.listTopicsByKeyWord("từ vựng", pageable);
        assertThat(page1.getContent()).hasSize(1);
        assertThat(page1.getContent().get(0).getTitle()).isEqualTo("JOB");

        Page<TopicHaveCourseDto> page2 = topicRepo.listTopicsByKeyWord("Let go", pageable);
        assertThat(page2.getContent()).hasSize(1);
        assertThat(page2.getContent().get(0).getCourse().getCategory().getTitle()).isEqualTo("từ vựng");

    }

    @Test
    void find_topic_have_no_course() {

        List<TopicHaveCourseDto> list = topicRepo.getTopicsWithNoCourse();
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getTitle()).isEqualTo("FAMILY");
    }
}
