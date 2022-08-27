package com.example.myvocab.serviceTest;


import com.example.myvocab.dto.CommentDto;
import com.example.myvocab.exception.NotFoundException;
import com.example.myvocab.model.*;
import com.example.myvocab.repo.CommentsRepo;
import com.example.myvocab.repo.UserTopicRepo;
import com.example.myvocab.repo.UsersRepo;
import com.example.myvocab.request.CommentRequest;
import com.example.myvocab.service.UserLearningService;
import com.example.myvocab.util.TimeStampFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CommentTest {
    @MockBean
    private CommentsRepo commentsRepo;

    @InjectMocks
    private UserLearningService service;



    @Test
    void sort_comment(){
        Long topicId=1L;

        Mockito.when(commentsRepo.findByUserTopic_Topic_Id(topicId)).thenReturn(createComments());
        List<CommentDto> commentDtos=service.getAllCommentsByTopic(topicId);

        assertThat(commentDtos).hasSize(4);
        assertThat(commentDtos.get(1).getMessage()).isEqualTo("bình luận 3");
        assertThat(commentDtos.get(3).getUserName()).isEqualTo("user2");

    }

    private List<Comments> createComments(){
        Users u1=Users.builder().id("1").fullName("user1").avatar("a1").build();
        Users u2=Users.builder().id("2").fullName("user2").avatar("a2").build();
        Users u3=Users.builder().id("3").fullName("user3").avatar("a3").build();
        Users u4=Users.builder().id("4").fullName("user4").avatar("a4").build();
        Course course=Course.builder().id(1L).build();
        Topic topic= Topic.builder().id(1L).build();
        Comments c1=Comments.builder()
                .id(1L)
                .userTopic(UserTopic.builder().topic(topic).userCourse(UserCourse.builder().course(course).user(u1).build()).build())
                .idParent(null)
                .message("bình luận 1")
                .createdAt(LocalDateTime.now().minusMinutes(4))
                .build();
        Comments c2=Comments.builder()
                .id(2L)
                .userTopic(UserTopic.builder().topic(topic).userCourse(UserCourse.builder().course(course).user(u2).build()).build())
                .idParent(null)
                .message("bình luận 2")
                .createdAt(LocalDateTime.now().minusMinutes(3))
                .build();
        Comments c3=Comments.builder()
                .id(3L)
                .userTopic(UserTopic.builder().topic(topic).userCourse(UserCourse.builder().course(course).user(u3).build()).build())
                .idParent(1L)
                .message("bình luận 3")
                .createdAt(LocalDateTime.now().minusMinutes(2))
                .build();
        Comments c4=Comments.builder()
                .id(4L)
                .userTopic(UserTopic.builder().topic(topic).userCourse(UserCourse.builder().course(course).user(u4).build()).build())
                .idParent(1L)
                .message("bình luận 4")
                .createdAt(LocalDateTime.now().minusMinutes(1))
                .build();
        return List.of(c1,c2,c3,c4);

    }

}
