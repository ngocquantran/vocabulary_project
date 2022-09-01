package com.example.myvocab.controller;

import com.example.myvocab.dto.ChooseVocabDto;
import com.example.myvocab.dto.CommentDto;
import com.example.myvocab.dto.ContextDto;
import com.example.myvocab.model.Topic;
import com.example.myvocab.model.Users;
import com.example.myvocab.repo.UsersRepo;
import com.example.myvocab.security.AuthenticationEntryPointCustom;
import com.example.myvocab.security.UserDetailsCustom;
import com.example.myvocab.security.UserDetailsServiceCustom;
import com.example.myvocab.service.UserLearningService;
import com.example.myvocab.service.UserService;
import com.example.myvocab.service.ViewService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(WebController.class)
public class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsersRepo usersRepo;
    @MockBean
    private UserDetailsServiceCustom userDetailsServiceCustom;
    @MockBean
    private AuthenticationEntryPointCustom authenticationEntryPointCustom;
    @MockBean
    private ViewService viewService;
    @MockBean
    private UserService userService;
    @MockBean
    private UserLearningService userLearningService;

    @Test
    void getContextBySentence() throws Exception {
        ContextDto c1 = ContextDto.builder().id(1L).name("Alice").build();
        ContextDto c2 = ContextDto.builder().id(2L).name("Mike").build();
        ContextDto c3 = ContextDto.builder().id(3L).name("Sarah").build();
        List<ContextDto> list = List.of(c1, c2, c3);
        Mockito.when(userLearningService.getContextsBySentence(Mockito.anyLong())).thenReturn(list);

        String url = "/api/sentence/1/context";
        MvcResult mvcResult = mockMvc.perform(get(url).with(user("user"))).andExpect(status().isOk()).andReturn();

        String actualJsonResponse = mvcResult.getResponse().getContentAsString();


        String expectedJsonResponse = objectMapper.writeValueAsString(list);
        assertThat(actualJsonResponse).isEqualTo(expectedJsonResponse);

    }

    @Test
    void getAllTopicComments() throws Exception {
        Topic topic = Topic.builder().id(1L).title("chủ đề").build();
        Mockito.when(userLearningService.isTopicExist(Mockito.anyLong())).thenReturn(topic);

        CommentDto c1 = CommentDto.builder().id(1L).idParent(null).message("bình luận 1").createdAt("vừa xong").build();
        CommentDto c2 = CommentDto.builder().id(2L).idParent(null).message("bình luận 2").createdAt("vừa xong").build();
        List<CommentDto> list = List.of(c1, c2);
        Mockito.when(userLearningService.getAllCommentsByTopic(Mockito.anyLong())).thenReturn(list);

        String url = "/api/topic-comments?id=1";
        MvcResult mvcResult = mockMvc.perform(get(url).with(user("user"))).andExpect(status().isOk()).andReturn();
        String actualJsonResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        System.out.println(actualJsonResponse);

        String expectedJsonResponse = objectMapper.writeValueAsString(list);
        assertThat(actualJsonResponse).isEqualTo(expectedJsonResponse);

    }

//    @Test
//    @WithMockUser(username="admin",roles={"USER_NORMAL","ADMIN"})
//    void getVocabTopicToChooseToLearn() throws Exception {
//        Users user=Users.builder().id("123").build();
//        UserDetailsCustom userDetails = UserDetailsCustom.builder().user(user).build();
//
//        ChooseVocabDto c1 = ChooseVocabDto.builder().vocabId(1L).word("love").status(true).build();
//        ChooseVocabDto c2 = ChooseVocabDto.builder().vocabId(2L).word("book").status(false).build();
//        ChooseVocabDto c3 = ChooseVocabDto.builder().vocabId(3L).word("train").status(false).build();
//        List<ChooseVocabDto> list = List.of(c1, c2, c3);
//        Mockito.when(userLearningService.getTopicVocabToChoose(Mockito.anyLong(), eq(userDetails.getUser().getId()))).thenReturn(list);
//
//        String url = "/api/topic-vocab-to-learn?id=1";
//        MvcResult mvcResult = mockMvc.perform(get(url))
//                .andExpect(status().isOk()).andReturn();
//        String actualJsonResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
//        System.out.println(actualJsonResponse);
//
//        String expectedJsonResponse = objectMapper.writeValueAsString(list);
//        assertThat(actualJsonResponse).isEqualTo(expectedJsonResponse);
//    }
//



}
