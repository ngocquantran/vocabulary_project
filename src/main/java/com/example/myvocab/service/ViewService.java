package com.example.myvocab.service;

import com.example.myvocab.dto.CourseByCategoryDto;
import com.example.myvocab.dto.FilterVocabDto;
import com.example.myvocab.dto.OrdersInfo;
import com.example.myvocab.dto.TopicToCourseDto;
import com.example.myvocab.exception.BadRequestException;
import com.example.myvocab.exception.NotFoundException;
import com.example.myvocab.model.*;
import com.example.myvocab.model.Package;
import com.example.myvocab.model.enummodel.CourseStatus;
import com.example.myvocab.model.enummodel.OrderStatus;
import com.example.myvocab.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ViewService {
    @Autowired
    private CourseRepo courseRepo;
    @Autowired
    private TopicRepo topicRepo;
    @Autowired
    private UserTopicRepo userTopicRepo;
    @Autowired
    private VocabRepo vocabRepo;

    @Autowired
    private LevelsRepo levelsRepo;

    @Autowired
    private CourseCategoryRepo categoryRepo;

    @Autowired
    private PackageRepo packageRepo;

    @Autowired
    private OrdersRepo ordersRepo;


    public List<CourseGroup> getGroupsByCategoryId(Long categoryId) {
        return courseRepo.getGroupByCourseCategory(categoryId);
    }


    public List<CourseByCategoryDto> getAllCourseWithGroupByCategory(Long categoryId) {
        CourseCategory category = isCategoryExist(categoryId);
        List<CourseGroup> groups = courseRepo.getGroupByCourseCategory(categoryId);
        return groups.stream()
                .map(group -> CourseByCategoryDto
                        .builder()
                        .id(group.getId())
                        .title(group.getTitle())
                        .courses(courseRepo.findByCategory_IdAndGroup_IdAndStatus(categoryId, group.getId(), CourseStatus.PUBLIC))
                        .build())
                .collect(Collectors.toList());

    }

    public Course getCourseById(Long id) {
        Optional<Course> course = courseRepo.findCourseById(id);
        if (!course.isPresent()) {
            throw new NotFoundException("Không có khóa học id = " + id);
        }
        return course.get();
    }


    public List<Topic> getTopicByCourseId(Long id) {
        return topicRepo.findByCourse_IdOrderByIdAsc(id);
    }


    public List<UserTopic> getUserTopicByCourseIdAndUserId(Long courseId, String userId) {
        return userTopicRepo.findByUserCourse_Course_IdAndUserCourse_User_Id(courseId, userId);
    }




    public TopicToCourseDto getCourseFromTopic(Long topicId) {
        Optional<TopicToCourseDto> topic = topicRepo.findTopicById(topicId, TopicToCourseDto.class);
        if (topic.isEmpty()) {
            throw new NotFoundException("Không tìm thấy topic có id = " + topicId);
        }
        if (topic.get().getCourse().getStatus() == CourseStatus.PRIVATE) {   //check course private or not Không cho User truy cập
            throw new BadRequestException("");
        }
        return topic.get();
    }

    public List<Levels> getAllLevels() {
        return levelsRepo.findAll();
    }

    public List<CourseCategory> getAllCategory() {
        return categoryRepo.findAll();
    }


    public CourseCategory isCategoryExist(Long categoryId) {
        Optional<CourseCategory> o_courseCat = categoryRepo.findById(categoryId);
        if (o_courseCat.isEmpty()) {
            throw new NotFoundException("Không tìm thấy category có id = " + categoryId);
        }
        return o_courseCat.get();
    }

    public List<Package> getAllPackages() {
        return packageRepo.findAll();
    }

    public OrdersInfo getOrderById(Long orderId) {
        Optional<OrdersInfo> o_order = ordersRepo.findOrderInfoById(orderId);
        if (o_order.isEmpty()) {
            throw new NotFoundException("Không tìm thấy order id= " + orderId);
        }
        return o_order.get();
    }

    public List<OrdersInfo> getAllOrderByUser(String userId) {
        return ordersRepo.findByUser_Id(userId);
    }

    public List<OrdersInfo> getActiveOrderByUser(String userId, OrderStatus status) {
        return ordersRepo.findByUser_IdAndStatus(userId, status, OrdersInfo.class);
    }

}
