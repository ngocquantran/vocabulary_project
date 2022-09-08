package com.example.myvocab.controller;


import com.example.myvocab.model.Course;
import com.example.myvocab.model.CourseGroup;
import com.example.myvocab.request.CourseAddRequest;
import com.example.myvocab.request.TopicAddRequest;
import com.example.myvocab.request.VocabAddRequest;
import com.example.myvocab.service.AdminService;
import com.example.myvocab.service.ViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/admin/api/")
public class AdminApiController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private ViewService viewService;


    @PostMapping("edit-course")
    public ResponseEntity<?> editCourse(@RequestParam(value = "id") Long courseId, @RequestBody CourseAddRequest request) {
        System.out.println(request);
        adminService.handleEditCourseRequest(courseId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("edit-vocab")
    public ResponseEntity<?> editVocab(@RequestParam(value = "id") Long vocabId, @RequestBody VocabAddRequest request) {
        System.out.println(request);
        adminService.handleEditVocabRequest(vocabId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping(value = "add-vocab", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> addVocab(@RequestPart(value = "vocab") VocabAddRequest request,
                                      @RequestPart(value = "img") MultipartFile img,
                                      @RequestPart(value = "audio") MultipartFile audio,
                                      @RequestPart(value = "senAudio") MultipartFile senAudio) {
        adminService.addVocab(request, img, audio, senAudio);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("add-topic")
    public ResponseEntity<?> addTopic(@RequestBody TopicAddRequest request) {
        adminService.handleAddTopicRequest(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("delete-topic")
    public ResponseEntity<?> deleteTopic(@RequestParam(value = "id") Long topicId) {
        adminService.deleteTopic(topicId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping(value = "add-course", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> addCourse(@RequestPart(value = "course") CourseAddRequest request,
                                       @RequestPart(value = "img") MultipartFile file) {

        adminService.handleAddCourseRequest(request, file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("delete-course")
    public ResponseEntity<?> deleteCourse(@RequestParam(value = "id") Long courseId) {
        adminService.deleteCourse(courseId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("groups")
    public List<CourseGroup> getGroupByCategoryId(@RequestParam(value = "id") Long categoryId) {
        return viewService.getGroupsByCategoryId(categoryId);
    }

    @PostMapping("active-order")
    public ResponseEntity<?> activePendingOrder(@RequestParam(value = "id") Long orderId) {
        System.out.println(orderId);
        adminService.activeOrder(orderId);

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
