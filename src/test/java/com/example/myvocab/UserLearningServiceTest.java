package com.example.myvocab;


import com.example.myvocab.config.TestJpaConfig;
import com.example.myvocab.service.UserLearningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.transaction.Transactional;

@DataJpaTest
@ContextConfiguration(
        classes = {TestJpaConfig.class},
        loader = AnnotationConfigContextLoader.class
)
@Transactional
public class UserLearningServiceTest {

    @Autowired
    private UserLearningService service;

    @TestConfiguration
    static class UserLearningServiceImplTestContextConfiguration {
        @Bean
        public UserLearningService userLearningService() {
            return new UserLearningService();
        }
    }


}
