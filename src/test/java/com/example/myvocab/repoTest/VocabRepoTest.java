package com.example.myvocab.repoTest;

import com.example.myvocab.config.TestJpaConfig;
import com.example.myvocab.model.*;
import com.example.myvocab.repo.VocabRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;


import static org.assertj.core.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DataJpaTest
@ContextConfiguration(
        classes = {TestJpaConfig.class},
        loader = AnnotationConfigContextLoader.class
)
@Transactional
public class VocabRepoTest {
    @Autowired
    private TestEntityManager entityManager;
    @Resource
    private VocabRepo vocabRepo;


    @BeforeEach
    void create_vocab() {
        CourseGroup group1 = CourseGroup.builder().title("Khóa học miễn phí").build();
        CourseCategory category1 = CourseCategory.builder().title("từ vựng").img("").build();
        Levels level1 = Levels.builder().title("A0").build();

        Course course = Course.builder()
                .title("Let go")
                .category(category1)
                .group(group1)
                .levels(List.of(level1))
                .build();

        Topic topic = Topic.builder()
                .title("JOB")
                .img("")
                .course(course)
                .vocabs(new ArrayList<>())
                .build();


        Vocab v1 = Vocab.builder().word("love").type("(n)").phonetic("love").vnMeaning("yêu").topics(List.of(topic)).build();
        Vocab v2 = Vocab.builder().word("run").type("(v)").phonetic("run").vnMeaning("chạy").topics(List.of(topic)).build();
        Vocab savedV1 = entityManager.persist(v1);
        Vocab savedV2 = entityManager.persist(v2);
        topic.addVocab(savedV1);
        topic.addVocab(savedV2);
        entityManager.persist(topic);
        entityManager.flush();
    }


    @Test
    void find_by_topicId() {
        List<Vocab> vocabs = vocabRepo.findByTopics_Id(1L);
        assertThat(vocabs).hasSize(2);

    }

    @Test
    void find_by_courseId() {
        List<Vocab> vocabs = vocabRepo.findByTopics_Course_Id(1L);
        assertThat(vocabs).hasSize(2);

    }

    @Test
    void list_vocab_by_keyword() {

        Pageable pageable = PageRequest.of(0, 5);
        Page<Vocab> page1 = vocabRepo.listVocabByKeyWord("love", pageable);
        assertThat(page1.getContent()).hasSize(1);

        Page<Vocab> page2 = vocabRepo.listVocabByKeyWord("v", pageable);
        assertThat(page2.getTotalElements()).isEqualTo(2);

        Page<Vocab> page3 = vocabRepo.listVocabByKeyWord("adj", pageable);
        assertThat(page3.getContent()).hasSize(0);

    }
}
