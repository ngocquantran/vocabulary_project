package com.example.myvocab;

import com.example.myvocab.config.TestJpaConfig;
import com.example.myvocab.model.Roles;
import com.example.myvocab.model.UserRole;
import com.example.myvocab.model.Users;
import com.example.myvocab.model.enummodel.UserState;
import com.example.myvocab.repo.UsersRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.test.context.ContextConfiguration;

import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(
        classes = {TestJpaConfig.class},
        loader = AnnotationConfigContextLoader.class
)
@Transactional
public class UserRepoTest {
    @Autowired
    private TestEntityManager entityManager;

    @Resource
    private UsersRepo usersRepo;

    @BeforeEach
    void creatUser() {
        BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
        Users user1 = Users.builder()
                .email("a@gmail.com")
                .password(passwordEncoder.encode("12345678"))
                .fullName("Trần Văn A")
                .avatar("avatar")
                .build();
        Users user2 = Users.builder()
                .email("b@gmail.com")
                .password(passwordEncoder.encode("12345678"))
                .fullName("Trần Văn B")
                .avatar("avatar")
                .build();
        entityManager.persist(user1);
        entityManager.persist(user2);

        Roles role1 = Roles.builder().name("USER_NORMAL").build();
        Roles role2 = Roles.builder().name("USER_VIP").build();
        entityManager.persist(role1);
        entityManager.persist(role2);


        UserRole userRole1 = UserRole.builder()
                .user(user1)
                .role(role1)
                .build();

        UserRole userRole2 = UserRole.builder()
                .user(user2)
                .role(role2)
                .build();
        entityManager.persist(userRole1);
        entityManager.persist(userRole2);

        entityManager.flush();
    }

    @Test
    void findUserByEmail() {
        BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
        Users user = usersRepo.findByEmail("a@gmail.com").get();
        System.out.println(user.getId());
        assertThat(user.getId()).isNotNull();
        assertThat(user.getFullName()).isEqualTo("Trần Văn A");
        assertThat(passwordEncoder.matches("12345678", user.getPassword())).isTrue();

    }

    @Test
    void findUserByNameEmailOrRole() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Users> page1 = usersRepo.listUsersByKeyWord("Văn A", pageable);
        assertThat(page1.getContent()).hasSize(1);

        Page<Users> page2 = usersRepo.listUsersByKeyWord("Văn", pageable);
        assertThat(page2.getContent()).hasSize(2);

        Page<Users> page3 = usersRepo.listUsersByKeyWord("USER", pageable);
        assertThat(page3.getContent()).hasSize(2);

        Page<Users> page4 = usersRepo.listUsersByKeyWord("VIP", pageable);
        assertThat(page4.getContent()).hasSize(1);


    }


}
