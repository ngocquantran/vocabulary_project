package com.example.myvocab.repoTest;


import com.example.myvocab.config.TestJpaConfig;
import com.example.myvocab.dto.OrdersInfo;
import com.example.myvocab.model.Orders;
import com.example.myvocab.model.Package;
import com.example.myvocab.model.Users;
import com.example.myvocab.model.enummodel.OrderStatus;
import com.example.myvocab.repo.OrdersRepo;
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
import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(
        classes = {TestJpaConfig.class},
        loader = AnnotationConfigContextLoader.class
)
@Transactional
public class OrderRepoTest {
    @Autowired
    private TestEntityManager entityManager;


    @Resource
    private OrdersRepo ordersRepo;

    @BeforeEach
    void create_order() {
        BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
        Users user1 = Users.builder()
                .email("a@gmail.com")
                .password(passwordEncoder.encode("12345678"))
                .fullName("Trần Văn A")
                .avatar("avatar")
                .build();

        Package package1 = Package.builder()
                .title("Khóa học 3 tháng")
                .description("")
                .duration(3)
                .pricePerMonth(3000000L)
                .type("short")
                .build();

        Orders order = Orders.builder()
                .status(OrderStatus.PENDING)
                .user(user1)
                .aPackage(package1)
                .build();

        entityManager.persist(order);
        entityManager.flush();

    }

    @Test
    void find_order_byStatus_and_keyword() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<OrdersInfo> page1 = ordersRepo.findByStatusOrderByOrderDateDesc(OrderStatus.PENDING, pageable);
        assertThat(page1.getContent()).hasSize(1);

        Page<OrdersInfo> page2 = ordersRepo.findByStatusOrderByOrderDateDesc(OrderStatus.ACTIVATED, pageable);
        assertThat(page2.getContent()).hasSize(0);

        Page<OrdersInfo> page3 = ordersRepo.findByStatusAndKeyWord(OrderStatus.ACTIVATED, "3 tháng", pageable);
        assertThat(page3.getContent()).hasSize(0);

        Page<OrdersInfo> page4 = ordersRepo.findByStatusAndKeyWord(OrderStatus.PENDING, "3 tháng", pageable);
        assertThat(page4.getContent()).hasSize(1);

        Page<OrdersInfo> page5 = ordersRepo.findByStatusAndKeyWord(OrderStatus.PENDING, "Trần Văn A", pageable);
        assertThat(page5.getContent()).hasSize(1);

        Page<OrdersInfo> page6 = ordersRepo.findByStatusAndKeyWord(OrderStatus.PENDING, "b@gmail.com", pageable);
        assertThat(page6.getContent()).hasSize(0);
    }
}
