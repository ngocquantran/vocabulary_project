package com.example.myvocab.controller;

import com.example.myvocab.dto.*;
import com.example.myvocab.exception.BadRequestException;
import com.example.myvocab.exception.NotFoundException;
import com.example.myvocab.model.*;
import com.example.myvocab.model.Package;
import com.example.myvocab.model.enummodel.CourseStatus;
import com.example.myvocab.model.enummodel.TopicState;
import com.example.myvocab.repo.UsersRepo;
import com.example.myvocab.request.*;
import com.example.myvocab.security.UserDetailsCustom;
import com.example.myvocab.service.UserLearningService;
import com.example.myvocab.service.UserService;
import com.example.myvocab.service.ViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class WebController {
    @Autowired
    private ViewService viewService;
    @Autowired
    private UserLearningService userLearningService;
    @Autowired
    private UserService userService;
    @Autowired
    private UsersRepo usersRepo;

    @GetMapping("/")
    public String getHomePage(Model model,
                              @RequestParam(name = "category", defaultValue = "1") Long categoryId,
                              @AuthenticationPrincipal UserDetailsCustom userDetailsCustom) {

        List<CourseCategory> categories = viewService.getAllCategory();
        model.addAttribute("categories", categories);

        List<Levels> levels = viewService.getAllLevels();
        model.addAttribute("levels", levels);

        model.addAttribute("cateId", categoryId);

        List<CourseByCategoryDto> groupsWithCourses = viewService.getAllCourseWithGroupByCategory(categoryId);
        model.addAttribute("groupsWithCourses", groupsWithCourses);

        if (userDetailsCustom != null) {
            List<UserCourseInfo> userCourses = userService.getUserCourseByUserAndCategory(userDetailsCustom.getUser().getId(), categoryId);
            model.addAttribute("userCourses", userCourses);
        }
        return "web/index";
    }


    @GetMapping("/course")
    public String getCoursePage(Model model,
                                @RequestParam(name = "id") Long courseId,
                                @AuthenticationPrincipal UserDetailsCustom userDetails) {

        model.addAttribute("authorities", SecurityContextHolder.getContext().getAuthentication().getAuthorities());

        Course course = viewService.getCourseById(courseId);
        if (course.getStatus() == CourseStatus.PRIVATE) {   //check course private or not
            return "error/404";
        }
        model.addAttribute("curCourse", course);

        List<Topic> topics = viewService.getTopicByCourseId(courseId);
        model.addAttribute("topics", topics);

        if (userDetails != null) {
            UserCourse userCourse = userLearningService.getUserCourseAcceptNull(courseId, userDetails.getUser().getId());
            model.addAttribute("userCourse", userCourse);

            List<UserTopic> userTopics = viewService.getUserTopicByCourseIdAndUserId(courseId, userDetails.getUser().getId());
            model.addAttribute("userTopics", userTopics);
        }

        return "web/course";
    }

    @GetMapping("/filter")
    public String getFilterPage(Model model, @RequestParam(name = "id") Long topicId) {
        Users user = ((UserDetailsCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        //Check if user can access topic or not
        if (!userLearningService.generalCheckUserAccessToTopic(topicId, user.getId())) {
            return "error/403";
        }

        TopicToCourseDto topicToCourseDto = viewService.getCourseFromTopic(topicId);
        model.addAttribute("topicCourse", topicToCourseDto);

        List<Vocab> vocabs = userLearningService.getTopicVocabs(topicId);//--------------------
        model.addAttribute("vocabs", vocabs);

        model.addAttribute("topicId", topicId);

        return "web/filter";
    }


    @PostMapping(value = "/api/filter", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> submitFilterVocabResult(@RequestParam(name = "id") Long topicId,
                                                     @RequestBody List<FilterVocabRequest> requests) {
        Users user = usersRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        //Check if user can access topic or not
        if (!userLearningService.generalCheckUserAccessToTopic(topicId, user.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Topic topic = userLearningService.isTopicExist(topicId);
        userLearningService.handleSubmittedFilterVocabResult(topic, user, requests);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/filter-result")
    public String getFilterResultPage(Model model, @RequestParam(name = "id") Long topicId,
                                      @AuthenticationPrincipal UserDetailsCustom userDetails) {
        //Check if user can access topic or not
        if (!userLearningService.generalCheckUserAccessToTopic(topicId, userDetails.getUser().getId())) {
            return "error/403";
        }

        TopicToCourseDto topicToCourseDto = viewService.getCourseFromTopic(topicId);
        model.addAttribute("topicCourse", topicToCourseDto);

        //Lấy danh sách trạng thái các từ để chọn học
        if (userDetails != null) {
            List<ChooseVocabDto> list = userLearningService.getTopicVocabToChoose(topicId, userDetails.getUser().getId());
            model.addAttribute("vocabsToChoose", list);
        }
        return "web/filter-result";
    }

    @PostMapping(value = "/api/filter-result", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postListOfChosedWordToLearn(@RequestParam(name = "id") Long topicId,
                                                         @RequestBody List<LearnVocabRequest> requests,
                                                         @AuthenticationPrincipal UserDetailsCustom userDetails) {

        //Check if user can access topic or not
        if (!userLearningService.generalCheckUserAccessToTopic(topicId, userDetails.getUser().getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Topic topic=userLearningService.isTopicExist(topicId);
        //Lấy danh sách từ đã chọn để học  và lưu vào database
        userLearningService.saveLearnRequestToUserTopicVocab(topic.getId(), userDetails.getUser().getId(), requests);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/learn/vocab")
    public String getLearningVocabPage(Model model, @RequestParam(name = "id") Long topicId,
                                       @AuthenticationPrincipal UserDetailsCustom userDetails) {
        //Check if user can access topic or not
        if (!userLearningService.generalCheckUserAccessToTopic(topicId, userDetails.getUser().getId())) {
            return "error/403";
        }

        //Lấy thông tin course từ topicId
        TopicToCourseDto topicToCourseDto = viewService.getCourseFromTopic(topicId);
        model.addAttribute("topicCourse", topicToCourseDto);

        // Lấy danh sách vocab có trạng thái để chọn học
        List<Vocab> vocabs = userLearningService.getTopicVocabsToLearn(topicId, userDetails.getUser().getId());
        model.addAttribute("vocabs", vocabs);
        return "web/learn-vocab";
    }

    @GetMapping("/learn/sentence")
    public String getLearningSentencePage(Model model, @RequestParam(name = "id") Long topicId,
                                          @AuthenticationPrincipal UserDetailsCustom userDetails) {
        //Check if user can access topic or not
        if (!userLearningService.generalCheckUserAccessToTopic(topicId, userDetails.getUser().getId())) {
            return "error/403";
        }

        //Lấy thông tin course từ topicId
        TopicToCourseDto topicToCourseDto = viewService.getCourseFromTopic(topicId);
        model.addAttribute("topicCourse", topicToCourseDto);

        // Lấy danh sách câu của topic để học
        List<Sentence> sentences = userLearningService.getTopicSentenceToLearn(topicId);
        model.addAttribute("sentences", sentences);

        return "web/learn-sen";
    }


    @GetMapping("/api/sentence/{id}/context")
    public ResponseEntity<?> getContextBySentence(@PathVariable("id") Long sentenceId) {
        List<ContextDto> list = userLearningService.getContextsBySentence(sentenceId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }


    @GetMapping("/test/vocab")
    public String getTestVocabPage(Model model, @RequestParam(name = "id") Long topicId,
                                   @AuthenticationPrincipal UserDetailsCustom userDetails) {
        //Check if user can access topic or not
        if (!userLearningService.generalCheckUserAccessToTopic(topicId, userDetails.getUser().getId())) {
            return "error/403";
        }

        TopicToCourseDto topicToCourseDto = viewService.getCourseFromTopic(topicId);
        model.addAttribute("topicCourse", topicToCourseDto);

        List<VocabTestDto> vocabs = userLearningService.getTestVocabs(topicId);
        model.addAttribute("vocabs", vocabs);

        return "web/test-vocab";
    }


    @PostMapping(value = "/api/vocab-test-result", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> submitVocabTestResult(@RequestParam(name = "id") Long topicId,
                                                   @RequestBody List<TestVocabResultRequest> requests,
                                                   @AuthenticationPrincipal UserDetailsCustom userDetails) {
        //Check if user can access topic or not
        if (!userLearningService.generalCheckUserAccessToTopic(topicId, userDetails.getUser().getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        userLearningService.handleVocabTestResult(topicId, userDetails.getUser().getId(), requests);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/api/sentence-test-result", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> submitSentenceTestResult(@RequestParam(name = "id") Long topicId,
                                                      @RequestBody List<TestSenResultRequest> requests) {
        Users user = usersRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        //Check if user can access topic or not
        if (!userLearningService.generalCheckUserAccessToTopic(topicId, user.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        userLearningService.handleSentenceTestResult(topicId, user, requests);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/test/sentence")
    public String getTestSentencePage(Model model, @RequestParam(name = "id") Long topicId,
                                      @AuthenticationPrincipal UserDetailsCustom userDetails) {
        //Check if user can access topic or not
        if (!userLearningService.generalCheckUserAccessToTopic(topicId, userDetails.getUser().getId())) {
            return "error/403";
        }

        TopicToCourseDto topicToCourseDto = viewService.getCourseFromTopic(topicId);
        model.addAttribute("topicCourse", topicToCourseDto);

        // Lấy danh sách câu của topic để
        List<Sentence> sentences = userLearningService.getTopicSentenceToLearn(topicId);
        model.addAttribute("sentences", sentences);

        return "web/test-sen";
    }


    @GetMapping("/result")
    public String getResultPage(Model model, @RequestParam(name = "id") Long topicId,
                                @AuthenticationPrincipal UserDetailsCustom userDetails) {
        //Check if user can access topic or not
        if (!userLearningService.generalCheckUserAccessToTopic(topicId, userDetails.getUser().getId())) {
            return "error/403";
        }

        TopicToCourseDto topicToCourseDto = viewService.getCourseFromTopic(topicId);
        model.addAttribute("topicCourse", topicToCourseDto);

        //Lấy kết quả test tùy theo category là vocab hay sentence
        if (topicToCourseDto.getCourse().getCategory().getId() == 1) {
            List<VocabTestResultDto> testResults = userLearningService.getVocabsTestResult(topicId, userDetails.getUser().getId());
            model.addAttribute("testResults", testResults);
        } else if (topicToCourseDto.getCourse().getCategory().getId() == 2) {
            List<UserTopicSentence> testResults = userLearningService.getSenTestResult(topicId, userDetails.getUser().getId());
            model.addAttribute("testResults", testResults);
        }

        //List top ranks
        List<UserTopicRankDto> topRanks = userLearningService.getTopRank(topicId, userDetails.getUser().getId());

        //Top 8 ranks to render chart
        model.addAttribute("topRanks", topRanks.stream().limit(8).collect(Collectors.toList()));

        //get UserRank only
        model.addAttribute("userRank", topRanks.stream().filter(r -> r.getUserId().equals(userDetails.getUser().getId())).findFirst().get());


        //Lấy record của User theo topic để render biểu đồ
        List<UserTopicRecord> userTopicRecords = userLearningService.getTopicRecords(topicId, userDetails.getUser().getId());
        model.addAttribute("userTopicRecords", userTopicRecords);

        //Lấy UserTopicState để hiện thị thông báo qua bài
        TopicState state = userLearningService.getUserTopicState(topicId, userDetails.getUser().getId());
        model.addAttribute("state", state);


        return "web/result";
    }


    @GetMapping("/api/topic-comments")
    public ResponseEntity<?> getAllTopicComments(@RequestParam(name = "id") Long topicId) {
        Topic topic = userLearningService.isTopicExist(topicId);
        List<CommentDto> list = userLearningService.getAllCommentsByTopic(topic.getId());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping(value = "/api/topic-comments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createComment(@RequestParam(name = "id") Long topicId,
                                           @RequestBody CommentRequest request,

                                           @AuthenticationPrincipal UserDetailsCustom userDetails) {
        UserTopic userTopic = userLearningService.isUserTopicExist(topicId, userDetails.getUser().getId());
        Comments comment = userLearningService.createComment(userTopic, request);

        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }


    @GetMapping("/api/topic-vocab-to-learn")
    public ResponseEntity<?> getVocabTopicToChooseToLearn(@RequestParam(name = "id") Long topicId,
                                                          @AuthenticationPrincipal UserDetailsCustom userDetails) {
        List<ChooseVocabDto> list = userLearningService.getTopicVocabToChoose(topicId, userDetails.getUser().getId());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/login-form")
    public String getLoginPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "web/login";
        }
        return "redirect:/";
    }

    @GetMapping("/premium")
    public String getPremiumPage(Model model) {
        if (!userService.isNormalUser()) {
            return "redirect:/";
        }

        Users user = ((UserDetailsCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        UserOrderInfo userInfo = UserOrderInfo.builder()
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
        model.addAttribute("user", userInfo);

        List<Package> packages = viewService.getAllPackages();
        model.addAttribute("packages", packages);

        boolean isOrderPendingExist=userService.isUserOrderPendingExist(user.getId());
        model.addAttribute("isOrderPendingExist", isOrderPendingExist);

        return "web/premium";
    }

    @GetMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value() || statusCode == HttpStatus.BAD_REQUEST.value()) {
                return "error/404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "error/500";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "error/403";
            } else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
                return "error/401";
            }
        }
        return "error/error";
    }

    @ExceptionHandler({NotFoundException.class, BadRequestException.class})
    public String returnErrorPage() {
        return "error/404";
    }


}
