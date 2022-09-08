package com.example.myvocab.controller;

import com.example.myvocab.dto.*;
import com.example.myvocab.exception.NotFoundException;
import com.example.myvocab.filter.OderSpecificationBuilder;
import com.example.myvocab.model.*;
import com.example.myvocab.model.enummodel.OrderStatus;
import com.example.myvocab.repo.OrdersRepo;
import com.example.myvocab.service.AdminService;
import com.example.myvocab.service.UserLearningService;
import com.example.myvocab.service.UserService;
import com.example.myvocab.service.ViewService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class AdminViewController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private UserLearningService userLearningService;

    @Autowired
    private ViewService viewService;

    @Autowired
    private UserService userService;


    @GetMapping("/admin/orders")
    public String getOrderPage(Model model,
                               @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                               @RequestParam(name = "status", defaultValue = "PENDING") String status,
                               @RequestParam(name = "keyword", required = false) String keyword) {
        int pageSize = 5;
        OrderStatus orderStatus = adminService.getOrderStatus(status);
        if (orderStatus == null) {
            throw new NotFoundException("Status không tồn tại");
        }

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        PageDto pageDto = adminService.showAllOrderByPage(pageNum, keyword, pageable, orderStatus);
        model.addAttribute("pageInfo", pageDto);

        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);


        return "admin/orders";
    }

    @GetMapping("/admin/users")
    public String getUsersPage(Model model,
                               @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                               @RequestParam(name = "keyword", required = false) String keyword) {

        int pageSize = 5;

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        PageDto pageDto = adminService.showAllUserByPage(pageNum, keyword, pageable);
        model.addAttribute("pageInfo", pageDto);

        model.addAttribute("keyword", keyword);
        return "admin/users";
    }

    @GetMapping("/admin/courses")
    public String getCoursesPage(Model model,
                                 @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                 @RequestParam(name = "keyword", required = false) String keyword) {
        int pageSize = 5;

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        PageDto pageDto = adminService.showAllCourseByPage(pageNum, keyword, pageable);
        model.addAttribute("pageInfo", pageDto);

        model.addAttribute("keyword", keyword);


        return "admin/courses";
    }

    @GetMapping("/admin/vocabs")
    public String getVocabsPage(Model model,
                                @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                @RequestParam(name = "keyword", required = false) String keyword) {
        int pageSize = 5;

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        PageDto pageDto = adminService.showAllVocabByPage(pageNum, keyword, pageable);
        model.addAttribute("pageInfo", pageDto);
        model.addAttribute("keyword", keyword);

        return "admin/vocabs";
    }

    @GetMapping("/admin/sentences")
    public String getSentencePage(Model model,
                                  @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                  @RequestParam(name = "keyword", required = false) String keyword) {

        int pageSize = 5;

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        PageDto pageDto = adminService.showAllSentenceByPage(pageNum, keyword, pageable);
        model.addAttribute("pageInfo", pageDto);

        model.addAttribute("keyword", keyword);
        return "admin/sentences";
    }

    @GetMapping("/admin/topics")
    public String getTopicsPage(Model model,
                                @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                @RequestParam(name = "keyword", required = false) String keyword) {
        int pageSize = 5;

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        PageDto pageDto = adminService.showAllTopicsByPage(pageNum, keyword, pageable);
        model.addAttribute("pageInfo", pageDto);

        model.addAttribute("keyword", keyword);
        return "admin/topics";
    }

    @GetMapping("/admin/sentence-detail")
    public String getSentenceDetailPage(Model model, @RequestParam(name = "id") Long sentenceId) {
        Sentence sentence = userLearningService.isSentenceExist(sentenceId);
        model.addAttribute("sentence", sentence);

        List<ContextDto> contexts = userLearningService.getContextsBySentence(sentenceId);
        model.addAttribute("contexts", contexts);

        return "admin/sentence-detail";
    }

    @GetMapping("/admin/course-detail")
    public String getCourseDetailPage(Model model, @RequestParam(name = "id") Long courseId) {
        Course course = userLearningService.isCourseExist(courseId);
        model.addAttribute("course", course);

        List<Topic> topics = viewService.getTopicByCourseId(courseId);
        model.addAttribute("topics", topics);

        boolean isUserCourseExist=userLearningService.isUserCourseExist(courseId);
        model.addAttribute("isUserCourseExist", isUserCourseExist);

        return "admin/course-detail";
    }

    @GetMapping("/admin/user-detail")
    public String getUserDetailPage(Model model, @RequestParam(value = "id") String userId) {
        UserPersonalDto usersInfo = userService.getUserInfoFromId(userId);
        model.addAttribute("user", usersInfo);

        List<UserCourseInfo> userCourses = userService.getUserCourseByUser(userId);
        model.addAttribute("userCourses", userCourses);

        List<OrdersInfo> activeOrders = viewService.getActiveOrderByUser(userId, OrderStatus.ACTIVATED);
        model.addAttribute("activeOrders", activeOrders);

        return "admin/user-detail";
    }

    @GetMapping("/admin/topic-detail")
    public String getTopicDetailPage(Model model, @RequestParam(value = "id") Long topicId) {
        Topic topic = userLearningService.isTopicExist(topicId);
        model.addAttribute("topic", topic);

        if (topic.getNumberOfVocabs() > 0) {
            List<Vocab> vocabs = userLearningService.getTopicVocabs(topicId);
            model.addAttribute("vocabs", vocabs);
        } else {
            List<Sentence> sentences = userLearningService.getTopicSentenceToLearn(topicId);
            model.addAttribute("sentences", sentences);
        }
        return "admin/topic-detail";
    }

    @GetMapping("/admin/course-edit")
    public String getCourseEditPage(Model model, @RequestParam(value = "id") Long courseId) {
        Course course = userLearningService.isCourseExist(courseId);
        model.addAttribute("course", course);

        List<CourseGroup> groups = viewService.getGroupsByCategoryId(course.getCategory().getId());
        model.addAttribute("groups", groups);
        return "admin/course-edit";
    }

    @GetMapping("/admin/vocab-edit")
    public String getVocabEditPage(Model model, @RequestParam(value = "id") Long vocabId) {
        Vocab vocab = userLearningService.isVocabExist(vocabId);
        model.addAttribute("vocab", vocab);

        return "admin/vocab-edit";
    }

    @GetMapping("/admin/vocab-add")
    public String getVocabAddPage() {

        return "admin/vocab-add";
    }

    @GetMapping("/admin/topic-add")
    public String getTopicAddPage(Model model) {

        List<VocabPictureDto> vocabs = adminService.getAllVocabPicture();
        model.addAttribute("vocabs", vocabs);

        List<SentencePictureDto> sentences = adminService.getAllSentencePicture();
        model.addAttribute("sentences", sentences);

        return "admin/topic-add";
    }

    @GetMapping("/admin/course-add")
    public String getCourseAddPage(Model model) {
        List<CourseCategory> categories = viewService.getAllCategory();
        model.addAttribute("categories", categories);

        List<Levels> levels = viewService.getAllLevels();
        model.addAttribute("levels", levels);

        List<TopicHaveCourseDto> topics = adminService.getTopicsHaveNoCourse();
        model.addAttribute("topics", topics);

        return "admin/course-add";
    }


//    @GetMapping("/admin/orders")
//    public String getOrderPage(Model model,
//                               @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
//                               @RequestParam(name="status",defaultValue = "PENDING") String status,
//                               @RequestParam(name = "search",required = false) String search) {
//        OderSpecificationBuilder builder=new OderSpecificationBuilder();
//        Pattern pattern=Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");
//        Matcher matcher=pattern.matcher(search+",");
//        while (matcher.find()){
//            builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
//        }
//        Specification<Orders> specification=builder.build();
//        List<Orders> orders= ordersRepo.findAll(specification);
//        ProjectionFactory pf=new SpelAwareProxyProjectionFactory();
//        List<OrdersInfo> list=orders.stream().map(orders1 -> pf.createProjection(OrdersInfo.class,orders1) ).collect(Collectors.toList());
//        model.addAttribute("orders",list);
//
//        return "admin/orders";
//    }

}
