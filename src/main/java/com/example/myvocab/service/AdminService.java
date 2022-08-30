package com.example.myvocab.service;


import com.example.myvocab.dto.*;
import com.example.myvocab.exception.NotFoundException;
import com.example.myvocab.model.*;
import com.example.myvocab.model.enummodel.OrderStatus;
import com.example.myvocab.repo.*;
import com.example.myvocab.request.CourseAddRequest;
import com.example.myvocab.request.TopicAddRequest;
import com.example.myvocab.request.VocabAddRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {
    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private OrdersRepo ordersRepo;

    @Autowired
    private VocabRepo vocabRepo;

    @Autowired
    private SentenceRepo sentenceRepo;

    @Autowired
    private TopicRepo topicRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CourseGroupRepo groupRepo;

    @Autowired
    private LevelsRepo levelsRepo;

    @Autowired
    private FileService fileService;

    @Autowired
    private CourseCategoryRepo categoryRepo;

    @Autowired
    private RolesRepo rolesRepo;

    @Autowired
    private UserRoleRepo userRoleRepo;

    public PageDto getListOfDataByPage(int pageNum, Page<?> page) {
        PageDto pageDto = PageDto.builder()
                .currentPage(pageNum)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .dataList(page.getContent())
                .build();
        return pageDto;
    }

    public PageDto listAllDataByPageWithOrWithoutKeyword(int pageNum, String keyword, Page<?> pageWithNoKeyWord, Page<?> pageWithKeyWord) {
        if (keyword == null) {
            return getListOfDataByPage(pageNum, pageWithNoKeyWord);
        }
        return getListOfDataByPage(pageNum, pageWithKeyWord);
    }

    public PageDto showAllCourseByPage(int pageNum, String keyword, Pageable pageable) {
        return listAllDataByPageWithOrWithoutKeyword(pageNum, keyword, courseRepo.findAll(pageable), courseRepo.listCourseByKeyWord(keyword, pageable));
    }

    public PageDto showAllUserByPage(int pageNum, String keyword, Pageable pageable) {
        return listAllDataByPageWithOrWithoutKeyword(pageNum, keyword, usersRepo.findAll(pageable), usersRepo.listUsersByKeyWord(keyword, pageable));
    }

    public PageDto showAllOrderByPage(int pageNum, String keyword, Pageable pageable, OrderStatus status) {
        return listAllDataByPageWithOrWithoutKeyword(pageNum, keyword, ordersRepo.findByStatusOrderByOrderDateDesc(status, pageable), ordersRepo.findByStatusAndKeyWord(status, keyword, pageable));
    }

    public OrderStatus getOrderStatus(String status) {
        for (OrderStatus s : OrderStatus.values()) {
            if (status.equalsIgnoreCase(s.getCode())) {
                return s;
            }
        }
        return null;
    }

    public PageDto showAllVocabByPage(int pageNum, String keyword, Pageable pageable) {
        return listAllDataByPageWithOrWithoutKeyword(pageNum, keyword, vocabRepo.findAll(pageable), vocabRepo.listVocabByKeyWord(keyword, pageable));
    }

    public PageDto showAllSentenceByPage(int pageNum, String keyword, Pageable pageable) {
        return listAllDataByPageWithOrWithoutKeyword(pageNum, keyword, sentenceRepo.findAll(pageable), sentenceRepo.listSentenceByKeyWord(keyword, pageable));
    }

    public PageDto showAllTopicsByPage(int pageNum, String keyword, Pageable pageable) {
        return listAllDataByPageWithOrWithoutKeyword(pageNum, keyword, topicRepo.findAllTopic(pageable), topicRepo.listTopicsByKeyWord(keyword, pageable));
    }


    public void handleEditCourseRequest(Long courseId, CourseAddRequest request) {
        Optional<Course> o_course = courseRepo.findCourseById(courseId);
        if (o_course.isEmpty()) {
            throw new NotFoundException("Không tìm thấy course id=" + courseId);
        }
        Course course = o_course.get();
        course.setTitle(request.getTitle());
        course.setStatus(request.getStatus());
        CourseGroup group = groupRepo.findById(request.getGroupId()).get();
        course.setGroup(group);
        List<Levels> levels = request.getLevels().stream().map(s -> levelsRepo.findById(s).get()).collect(Collectors.toList());
        course.setLevels(levels);
        course.setDescription(request.getDescription());
        course.setGoal(request.getGoal());
        course.setContent(request.getContent());
        course.setTargetLearner(request.getTargetLearner());
        courseRepo.save(course);

    }


    public void handleAddCourseRequest(CourseAddRequest request, MultipartFile file) {
        Course course = Course.builder()
                .title(request.getTitle())
                .category(categoryRepo.findById(request.getCategoryId()).get())
                .status(request.getStatus())
                .group(groupRepo.findById(request.getGroupId()).get())
                .description(request.getDescription())
                .content(request.getContent())
                .goal(request.getGoal())
                .targetLearner(request.getTargetLearner())
                .levels(request.getLevels().stream().map(l -> levelsRepo.findById(l).get()).collect(Collectors.toList()))
                .build();
        Course savedCourse = courseRepo.save(course);


        String imgURL = handleUploadFile(file, "upload/img/course_thumb/", "course-thumb-" + savedCourse.getId());
        savedCourse.setThumbnail("/upload/img/course_thumb/" + imgURL);

        //Assign course for topics in request
        for (Long topicId : request.getTopics()) {
            Topic topic = topicRepo.findTopicById(topicId, Topic.class).get();
            topic.setCourse(savedCourse);
            topicRepo.save(topic);
        }

        courseRepo.save(savedCourse);
    }

    public void handleEditVocabRequest(Long vocabId, VocabAddRequest request) {
        Optional<Vocab> o_vocab = vocabRepo.findById(vocabId);
        if (o_vocab.isEmpty()) {
            throw new NotFoundException("Không tìm thấy vocab id" + vocabId);
        }
        Vocab vocab = o_vocab.get();
        vocab.setWord(request.getWord());
        vocab.setType(request.getType());
        vocab.setPhonetic(request.getPhonetic());
        vocab.setEnMeaning(request.getEnMeaning());
        vocab.setVnMeaning(request.getVnMeaning());
        vocab.setEnSentence(request.getEnSentence());
        vocab.setVnSentence(request.getVnSentence());
        vocabRepo.save(vocab);
    }

    public String handleUploadFile(MultipartFile file, String uploadDir, String name) {
        String fileName = name + file.getOriginalFilename().substring(file.getOriginalFilename().length() - 4);
//        user.setAvatar("/"+uploadDir+fileName);
        fileService.uploadFile(uploadDir, fileName, file);
//        return "/" + uploadDir + fileName; do lưu vào static nên chỉ lấy đường dẫn từ asset
        return fileName;
    }

    public void addVocab(VocabAddRequest request, MultipartFile img, MultipartFile audio, MultipartFile senAudio) {
        Vocab vocab = Vocab.builder()
                .img("")
                .word(request.getWord())
                .type(request.getType())
                .phonetic(request.getPhonetic())
                .enMeaning(request.getEnMeaning())
                .vnMeaning(request.getVnMeaning())
                .enSentence(request.getEnSentence())
                .vnSentence(request.getVnSentence())
                .audio("")
                .senAudio("")
                .build();
        Vocab savedVocab = vocabRepo.save(vocab);

        String imgURL = handleUploadFile(img, "upload/img/word/", savedVocab.getWord().replaceAll(" ", "-") + "-" + savedVocab.getId() + "-thumb");
        String audioURL = handleUploadFile(audio, "upload/mp3/word/pronounce/", savedVocab.getWord().replaceAll(" ", "-") + "-" + savedVocab.getId() + "-audio");
        String senAudioURL = handleUploadFile(senAudio, "upload/mp3/word/example/", savedVocab.getWord().replaceAll(" ", "-") + "-" + savedVocab.getId() + "-senAudio");

        savedVocab.setImg("/upload/img/word/" + imgURL);
        savedVocab.setAudio("/upload/mp3/word/pronounce/" + audioURL);
        savedVocab.setSenAudio("/upload/mp3/word/example/" + senAudioURL);

        vocabRepo.save(savedVocab);

    }

    public List<VocabPictureDto> getAllVocabPicture() {
        return vocabRepo.findAll().stream().map(vocab -> modelMapper.map(vocab, VocabPictureDto.class)).collect(Collectors.toList());
    }

    public List<SentencePictureDto> getAllSentencePicture() {
        return sentenceRepo.findAll().stream().map(sentence -> modelMapper.map(sentence, SentencePictureDto.class)).collect(Collectors.toList());
    }

    public void handleAddTopicRequest(TopicAddRequest request) {
        Topic topic = Topic.builder()
                .img(request.getImg())
                .title(request.getTitle())
                .vocabs(new ArrayList<>())
                .sentences(new ArrayList<>())
                .build();
        if (request.getType().equalsIgnoreCase("Từ vựng")) {
            for (Long id : request.getContent()) {
                Optional<Vocab> o_vocab = vocabRepo.findById(id);
                if (o_vocab.isEmpty()) {
                    throw new NotFoundException("Không tìm thấy vocab id =" + id);
                }
                topic.addVocab(o_vocab.get());
                o_vocab.get().addTopic(topic);
            }
        } else {
            for (Long id : request.getContent()) {
                Optional<Sentence> o_sen = sentenceRepo.findById(id);
                if (o_sen.isEmpty()) {
                    throw new NotFoundException("Không tìm thấy sentence id =" + id);
                }
                topic.addSentence(o_sen.get());
                o_sen.get().addTopic(topic);
            }
        }
        topicRepo.save(topic);
    }

    public List<TopicHaveCourseDto> getTopicsHaveNoCourse() {
        return topicRepo.getTopicsWithNoCourse();
    }

    public void activeOrder(Long orderId) {
        Optional<Orders> o_order = ordersRepo.findById(orderId);
        if (o_order.isEmpty()) {
            throw new NotFoundException("Không tìm thấy order Id =" + orderId);
        }
        //set status and active date of order
        Orders order = o_order.get();
        order.setStatus(OrderStatus.ACTIVATED);
        order.setActiveDate(LocalDate.now());
        Orders saveOrder = ordersRepo.save(order);

        //set userole to VIP
        Users user = saveOrder.getUser();
        Roles oldRole = rolesRepo.findByName("USER_NORMAL").get();
        Roles newRole = rolesRepo.findByName("USER_VIP").get();
        UserRole userRole = userRoleRepo.findByRole_IdAndUser_Id(oldRole.getId(), user.getId()).get();
        userRole.setRole(newRole);
        userRoleRepo.save(userRole);

    }


}
