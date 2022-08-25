package com.example.myvocab.service;

import com.example.myvocab.dto.*;
import com.example.myvocab.exception.BadRequestException;
import com.example.myvocab.exception.NotFoundException;
import com.example.myvocab.mapper.VocabTestMapper;
import com.example.myvocab.model.*;
import com.example.myvocab.model.enummodel.Gender;
import com.example.myvocab.model.enummodel.LearningStage;
import com.example.myvocab.model.enummodel.TopicState;
import com.example.myvocab.repo.*;
import com.example.myvocab.request.*;
import com.example.myvocab.security.UserDetailsCustom;
import com.example.myvocab.util.TimeStampFormat;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserLearningService {

    @Autowired
    UserTopicSentenceRepo userTopicSentenceRepo;
    @Autowired
    private UserCourseRepo userCourseRepo;
    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private CourseRepo courseRepo;
    @Autowired
    private TopicRepo topicRepo;
    @Autowired
    private UserTopicRepo userTopicRepo;
    @Autowired
    private VocabRepo vocabRepo;
    @Autowired
    private UserTopicVocabRepo userTopicVocabRepo;
    @Autowired
    private UserTopicRecordRepo userTopicRecordRepo;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SentenceRepo sentenceRepo;
    @Autowired
    private ContextRepo contextRepo;
    @Autowired
    private CommentsRepo commentsRepo;


    public UserCourse getUserCourseAcceptNull(Long courseId, String userId) {
        Optional<UserCourse> o_userCourse = userCourseRepo.findByCourse_IdAndUser_Id(courseId, userId);
        if (o_userCourse.isEmpty()) {
            return null;
        }
        return o_userCourse.get();
    }


    public UserCourse createUserCourse(Long courseId, String userId) {
        Users user = isUserExist(userId);
        Course course = isCourseExist(courseId);

        Optional<UserCourse> o_userCourse = userCourseRepo.findByCourse_IdAndUser_Id(courseId, userId);
        if (o_userCourse.isPresent()) {//Check if userCourse already exist => no more create
            return o_userCourse.get();
        }
        UserCourse userCourse = UserCourse.builder()
                .course(course)
                .user(user)
                .build();
        userCourseRepo.save(userCourse);

        return userCourse;
    }

    public UserTopic createPendingUserTopic(Long topicId, String userId) {
        Topic topic = isTopicExist(topicId);
        Course course = topic.getCourse();
        UserCourse userCourse = createUserCourse(course.getId(), userId);

        Optional<UserTopic> o_userTopic = userTopicRepo.findByTopic_IdAndUserCourse_User_Id(topicId, userId, UserTopic.class);
        if (o_userTopic.isPresent()) { // check if userTopic already exist => change to PENDING status
            UserTopic userTopic = o_userTopic.get();
            userTopic.setStatus(TopicState.PENDING);
            userTopicRepo.save(userTopic);
            return userTopic;
        }
        UserTopic userTopic = UserTopic.builder()  //If userTopic not yet existed => create new PENDING one
                .status(TopicState.PENDING)
                .topic(topic)
                .userCourse(userCourse)
                .build();
        userTopicRepo.save(userTopic);

        return userTopic;
    }


    public UserTopicRecord createUserTopicRecordByStage(Long topicId, String userId, LearningStage stage) {
        UserTopic userTopic = isUserTopicExist(topicId, userId);

        Optional<UserTopicRecord> o_userTopicRecord = userTopicRecordRepo.findByStageAndUserTopic_Id(stage, userTopic.getId());
        if (o_userTopicRecord.isPresent()) {
            return o_userTopicRecord.get();
        }
        UserTopicRecord userTopicRecord = UserTopicRecord.builder()
                .testTime(0)
                .rightAnswers(0)
                .totalAnswers(0)
                .stage(stage)
                .userTopic(userTopic)
                .build();
        userTopicRecordRepo.save(userTopicRecord);
        return userTopicRecord;
    }


    public void initUserTopicVocabs(Long topicId, String userId) {
        UserTopic userTopic = isUserTopicExist(topicId, userId);
        List<Vocab> vocabs = vocabRepo.findByTopics_Id(topicId);

        for (Vocab v : vocabs) {
            Optional<UserTopicVocab> o_userTopicVocab = userTopicVocabRepo.findByUserTopic_IdAndVocab_Id(userTopic.getId(), v.getId());
            if (o_userTopicVocab.isEmpty()) {

                UserTopicVocab userTopicVocab = UserTopicVocab.builder()
                        .userTopic(userTopic)
                        .vocab(v)
                        .build();
                userTopicVocabRepo.save(userTopicVocab);
            }
        }
    }

    public void handleSubmittedFilterVocabResult(Long topicId, String userId, List<FilterVocabRequest> requests) {
        UserTopic userTopic = createPendingUserTopic(topicId, userId);                                        //Initial userToping in Pending status
        UserTopicRecord userTopicRecord = createUserTopicRecordByStage(topicId, userId, LearningStage.FIRST);   // Initial UserTopicRecord in First stage
        initUserTopicVocabs(topicId, userId);                                                               //Create List of vocabs by topic for user

        Long numberOfRightAnswers = requests.stream().filter(vocabRequest -> vocabRequest.isStatus()).count();
        userTopicRecord.setRightAnswers(numberOfRightAnswers.intValue());                                   // Save known vocabs for Record stage First
        userTopicRecord.setTotalAnswers(requests.size());
        userTopicRecordRepo.save(userTopicRecord);

        //Update status of vocab from request into corresponding UserTopicVocab
        for (FilterVocabRequest v : requests) {
            Optional<UserTopicVocab> o_userTopicVocab = userTopicVocabRepo.findByUserTopic_IdAndVocab_Id(userTopic.getId(), v.getVocabId());
            if (o_userTopicVocab.isEmpty()) {
                throw new NotFoundException("Không tìm thấy từ vựng có id = " + v.getVocabId() + " trong topic có id = " + topicId);
            }
            UserTopicVocab userTopicVocab = o_userTopicVocab.get();
            userTopicVocab.setStatus(v.isStatus());
            userTopicVocabRepo.save(userTopicVocab);
        }

    }

    public List<Vocab> getTopicVocabs(Long topicId) {
        return vocabRepo.findByTopics_Id(topicId);
    }

    public List<ChooseVocabDto> getTopicVocabToChoose(Long topicId, String userId) {
        UserTopic userTopic = isUserTopicExist(topicId, userId);
        return userTopicVocabRepo.getTopicVocabsToChoose(userTopic.getId());
    }

    // Save list of vocab which user want to learn after choosing
    public void saveLearnRequestToUserTopicVocab(Long topicId, String userId, List<LearnVocabRequest> requests) {
        UserTopic userTopic = isUserTopicExist(topicId, userId);

        for (LearnVocabRequest v : requests) {
            Optional<UserTopicVocab> o_userTopicVocab = userTopicVocabRepo.findByUserTopic_IdAndVocab_Id(userTopic.getId(), v.getVocabId());
            if (o_userTopicVocab.isEmpty()) {
                throw new NotFoundException("Không tìm thấy từ vựng có id = " + v.getVocabId() + " trong topic có id = " + topicId);
            }
            UserTopicVocab userTopicVocab = o_userTopicVocab.get();
            userTopicVocab.setLearn(v.isLearn());
            userTopicVocabRepo.save(userTopicVocab);
        }

        //Update latest access timestamp of User to this course
        UserCourse userCourse = userTopic.getUserCourse();
        userCourse.setStudiedAt(LocalDate.now());
        userCourseRepo.save(userCourse);
    }

    // Get list of vocab want to learn
    public List<Vocab> getTopicVocabsToLearn(Long topicId, String userId) {
        UserTopic userTopic = isUserTopicExist(topicId, userId);
        List<Vocab> vocabs = userTopicVocabRepo.findByUserTopic_IdAndLearn(userTopic.getId(), true);
        return vocabs;
    }


//USER TESTING SERVICE----------------------------------------------------------------------------------------------------------------------------------


    //    Lấy danh sách từ vựng trong topic để render bài test

    public List<Sentence> getListOfSentenceForTest(Long topicId) {
        Topic topic = isTopicExist(topicId);
        return topic.getSentences().stream().toList();
    }


    public List<VocabTestDto> getTestVocabs(Long topicId) {
        Topic topic = isTopicExist(topicId);

        List<VocabTestDto> words = topic.getVocabs().stream().toList()
                .stream()
                .map(vocab -> VocabTestMapper.toVocabTest(vocab))
                .map(vocabTest -> renderVocabAnswers(vocabTest, topic.getCourse().getId()))
                .map(vocabTest -> renderVnMeaningAnswers(vocabTest, topic.getCourse().getId()))
                .map(vocabTest -> renderEnSentenceAnswers(vocabTest, topic.getCourse().getId()))
                .collect(Collectors.toList());

        Collections.shuffle(words);
        return words;
    }


//    Render answer list for VocabTest (Choosing 4 options)

    public VocabTestDto renderVocabAnswers(VocabTestDto vocabTest, Long courseId) {
        int answerIndex = vocabTest.getAnswerIndex();  //get random answer index
        List<String> vocabs = vocabRepo.findByTopics_Course_Id(courseId).stream().map(Vocab::getWord).collect(Collectors.toList());  //get all vocabs of course

        //render list 4 answer which the right answer have index = answerIndex
        List<String> wordLists = new ArrayList<>();
        int index = 0;
        boolean isContinue = true;
        Random rd = new Random();
        while (isContinue) {
            if (index + 1 == answerIndex) {
                wordLists.add(vocabTest.getWord());
                index++;
            } else {
                int i = rd.nextInt(vocabs.size());
                if (!vocabs.get(i).equals(vocabTest.getWord()) & !wordLists.contains(vocabs.get(i))) {
                    wordLists.add(vocabs.get(i));
                    index++;
                }
            }
            if (index >= 4) {
                isContinue = false;
            }
        }
        vocabTest.setVocabs(wordLists);
        return vocabTest;
    }


    public VocabTestDto renderVnMeaningAnswers(VocabTestDto vocabTest, Long courseId) {
        int answerIndex = vocabTest.getAnswerIndex();
        List<String> vnMeanings = vocabRepo.findByTopics_Course_Id(courseId).stream().map(Vocab::getVnMeaning).collect(Collectors.toList());
        List<String> vnLists = new ArrayList<>();
        int index = 0;
        boolean isContinue = true;
        Random rd = new Random();
        while (isContinue) {
            if (index + 1 == answerIndex) {
                vnLists.add(vocabTest.getVnMeaning());
                index++;
            } else {
                int i = rd.nextInt(vnMeanings.size());
                if (!vnMeanings.get(i).equals(vocabTest.getVnMeaning()) & !vnLists.contains(vnMeanings.get(i))) {
                    vnLists.add(vnMeanings.get(i));
                    index++;
                }
            }
            if (index >= 4) {
                isContinue = false;
            }
        }

        vocabTest.setVnMeanings(vnLists);
        return vocabTest;
    }

    public VocabTestDto renderEnSentenceAnswers(VocabTestDto vocabTest, Long courseId) {
        int answerIndex = vocabTest.getAnswerIndex();
        List<String> enSentences = vocabRepo.findByTopics_Course_Id(courseId).stream().map(Vocab::getEnSentence).collect(Collectors.toList());
        List<String> enLists = new ArrayList<>();
        int index = 0;
        boolean isContinue = true;
        Random rd = new Random();
        while (isContinue) {
            if (index + 1 == answerIndex) {
                enLists.add(vocabTest.getEnSentence());
                index++;
            } else {
                int i = rd.nextInt(enSentences.size());
                if (!enSentences.get(i).equals(vocabTest.getEnSentence()) & !enLists.contains(enSentences.get(i))) {
                    enLists.add(enSentences.get(i));
                    index++;
                }
            }
            if (index >= 4) {
                isContinue = false;
            }
        }
        vocabTest.setEnSentences(enLists);
        return vocabTest;
    }


    public void handleVocabTestResult(Long topicId, String userId, List<TestVocabResultRequest> requests) {
        UserTopic userTopic = isUserTopicExist(topicId, userId);

        UserTopicRecord recordNow = updateUserTopicRecordsFromTestResult(userTopic);
        updateTestVocabResult(userTopic, recordNow, requests);

        //Update latest access timestamp for userCourse
        updateLatestAccessTimestampOfUserCourse(userTopic);
    }

    public void handleSentenceTestResult(Long topicId, String userId, List<TestSenResultRequest> requests) {
        UserTopic userTopic = initialUserTopic(topicId, userId);

        // Initial userTopicRecord First stage - default parameter=0
        initialUserTopicRecordAtFirstStage(userTopic);

        UserTopicRecord recordNow = updateUserTopicRecordsFromTestResult(userTopic);
        updateTestSenResult(userTopic, recordNow, requests);

        //cập nhật userTopic và userCourse latest time
        updateLatestAccessTimestampOfUserCourse(userTopic);
    }

    public UserTopicRecord updateUserTopicRecordsFromTestResult(UserTopic userTopic) {
        // Check if this is the first test of topic or not by finding userTopicRecord stage NOW
        Optional<UserTopicRecord> o_recordNow = userTopicRecordRepo.findByStageAndUserTopic_Id(LearningStage.NOW, userTopic.getId());
        if (o_recordNow.isEmpty()) {     //If this is the first test => create userTopicRecord Now and update test result
            UserTopicRecord recordNow = createUserTopicRecordByStage(userTopic.getTopic().getId(), userTopic.getUserCourse().getUser().getId(), LearningStage.NOW);
            return recordNow;

        } else {    // if not the first test, update record of stage Best and previous before save new result to NOW stage
            UserTopicRecord recordBest = createUserTopicRecordByStage(userTopic.getTopic().getId(), userTopic.getUserCourse().getUser().getId(), LearningStage.BEST);
            UserTopicRecord recordPrev = createUserTopicRecordByStage(userTopic.getTopic().getId(), userTopic.getUserCourse().getUser().getId(), LearningStage.PREVIOUS);
            UserTopicRecord recordNow = o_recordNow.get();

            //  previous
            recordPrev.setTestTime(recordNow.getTestTime());
            recordPrev.setRightAnswers(recordNow.getRightAnswers());
            recordPrev.setTotalAnswers(recordNow.getTotalAnswers());
            userTopicRecordRepo.save(recordPrev);

            //  best
            if ((recordNow.getRightAnswers() == recordBest.getRightAnswers() && recordNow.getTestTime() < recordBest.getTestTime()) || recordNow.getRightAnswers() > recordBest.getRightAnswers()) {
                recordBest.setRightAnswers(recordNow.getRightAnswers());
                recordBest.setTestTime(recordNow.getTestTime());
                recordBest.setTotalAnswers(recordNow.getTotalAnswers());
                userTopicRecordRepo.save(recordBest);
            }
            return recordNow;
        }
    }

    public void updateLatestAccessTimestampOfUserCourse(UserTopic userTopic) {
        UserCourse userCourse = userTopic.getUserCourse();
        userCourse.setStudiedAt(LocalDate.now());
        userCourseRepo.save(userCourse);
    }

    public void updateTestVocabResult(UserTopic userTopic, UserTopicRecord recordNow, List<TestVocabResultRequest> requests) {

        int passVocabs = (int) requests.stream().filter(v -> v.isStatus()).count();
        int testTime = requests.stream().mapToInt(v -> v.getTestTime()).sum();

        updateUserTopicStatusFromTestResult(passVocabs, requests.size(), userTopic, recordNow, testTime);

        //Update UserTopicVocab
        updateUserTopicVocabAfterTest(requests, userTopic);

    }

    public void updateTestSenResult(UserTopic userTopic, UserTopicRecord recordNow, List<TestSenResultRequest> requests) {
        int passSens = (int) requests.stream().filter(s -> s.isStatus()).count();
        int testTime = requests.stream().mapToInt(v -> v.getTestTime()).sum();

        updateUserTopicStatusFromTestResult(passSens, requests.size(), userTopic, recordNow, testTime);

        //Update UserTopicSentence
        updateUserTopicSentence(userTopic, requests);
    }

    public void updateUserTopicStatusFromTestResult(int passEl, int totalEl, UserTopic userTopic, UserTopicRecord recordNow, int testTime) {
        if (passEl == totalEl) {
            if (userTopic.getStatus() != TopicState.PASS) {
                userTopic.setStatus(TopicState.PASS);                  // After taking test, if 100% correct set to PASS else set CONTINUE
                openNextUserTopic(userTopic);
            }

        } else {
            if (userTopic.getStatus() != TopicState.PASS) {
                userTopic.setStatus(TopicState.CONTINUE);
            }
        }
        userTopicRepo.save(userTopic);

        //Update test time and number of rightAnswer from request
        recordNow.setRightAnswers(passEl);
        recordNow.setTestTime(testTime);
        recordNow.setTotalAnswers(totalEl);
        userTopicRecordRepo.save(recordNow);
    }

    public void updateUserTopicVocabAfterTest(List<TestVocabResultRequest> requests, UserTopic userTopic) {
        for (TestVocabResultRequest v : requests) {
            Optional<UserTopicVocab> o_userTopicVocab = userTopicVocabRepo.findByUserTopic_IdAndVocab_Id(userTopic.getId(), v.getVocabId());
            if (o_userTopicVocab.isEmpty()) {
                throw new NotFoundException("Không tìm thấy từ vựng có id = " + v.getVocabId() + " trong userTopic có id = " + userTopic.getId());
            }
            UserTopicVocab userTopicVocab = o_userTopicVocab.get();
            userTopicVocab.setStatus(v.isStatus());
            userTopicVocabRepo.save(userTopicVocab);
        }
    }


    // Set next userTopic in course to stage NOW if previous topic PASS
    public void openNextUserTopic(UserTopic curUserTopic) {
        Course course = curUserTopic.getUserCourse().getCourse();
        List<Topic> topicList = topicRepo.findByCourse_IdOrderByIdAsc(course.getId());
        Topic curTopic = curUserTopic.getTopic();
        Long idNext = 0L;
        for (int i = 0; i < topicList.size(); i++) {
            if (topicList.get(i).getId() == curTopic.getId()) {
                idNext = topicList.get(i + 1).getId();
                break;
            }
        }
        Topic nextTopic = topicRepo.findTopicById(idNext, Topic.class).get();
        UserTopic userTopic = createPendingUserTopic(nextTopic.getId(), curUserTopic.getUserCourse().getUser().getId());
        userTopic.setStatus(TopicState.NOW);
        userTopicRepo.save(userTopic);

    }


    public List<VocabTestResultDto> getVocabsTestResult(Long topicId, String userId) {
        UserTopic userTopic = isUserTopicExist(topicId, userId);
        return userTopicVocabRepo.findByUserTopic_Id(userTopic.getId());
    }

    public List<UserTopicRecord> getTopicRecords(Long topicId, String userId) {
        UserTopic userTopic = isUserTopicExist(topicId, userId);
        return userTopicRecordRepo.findByUserTopic_Id(userTopic.getId());
    }

    public TopicState getUserTopicState(Long topicId, String userId) {
        UserTopic userTopic = isUserTopicExist(topicId, userId);
        return userTopic.getStatus();

    }


    public List<UserTopicRankDto> getTopRank(Long topicId, String userId) {
        Topic topic = isTopicExist(topicId);
        List<UserTopicRecord> recordList = userTopicRecordRepo.getTopRankOfUserTopic(topic.getId());
        List<UserTopicRankDto> rankDtoList = new ArrayList<>();

        boolean isUserIdInTop8 = false;
        //First take top 8 rank
        for (int i = 0; i < recordList.size(); i++) {
            UserTopicRankDto rankDto = UserTopicRankDto.builder()
                    .rank(i + 1)
                    .userName(recordList.get(i).getUserTopic().getUserCourse().getUser().getFullName())
                    .userId(recordList.get(i).getUserTopic().getUserCourse().getUser().getId())
                    .userImg(recordList.get(i).getUserTopic().getUserCourse().getUser().getAvatar())
                    .testTime(recordList.get(i).getTestTime())
                    .rightAnswers(recordList.get(i).getRightAnswers())
                    .build();
            rankDtoList.add(rankDto);
            if (recordList.get(i).getUserTopic().getUserCourse().getUser().getId().equals(userId)) {
                isUserIdInTop8 = true;
            }
            if (i == 7) {// In case record list size smaller than 8, so initial condition is recordList.size()
                break;
            }
        }
        //User rank if not in top 8
        if (!isUserIdInTop8) {
            rankDtoList.add(addUserTopicRankIfOutOfTop8(recordList, userId));
        }

        return rankDtoList;
    }

    public UserTopicRankDto addUserTopicRankIfOutOfTop8(List<UserTopicRecord> recordList, String userId) {
        for (int i = 0; i < recordList.size(); i++) {
            if (recordList.get(i).getUserTopic().getUserCourse().getUser().getId().equals(userId)) {
                return UserTopicRankDto.builder()
                        .rank(i + 1)
                        .userName(recordList.get(i).getUserTopic().getUserCourse().getUser().getFullName())
                        .userId(recordList.get(i).getUserTopic().getUserCourse().getUser().getId())
                        .userImg(recordList.get(i).getUserTopic().getUserCourse().getUser().getAvatar())
                        .testTime(recordList.get(i).getTestTime())
                        .rightAnswers(recordList.get(i).getRightAnswers())
                        .build();
            }
        }
        throw new NotFoundException("Không tìm thấy user id " + userId + " trong topic record");
    }

    public List<UserTopicRankDto> getTopTenRank(Long topicId) {
        Topic topic = isTopicExist(topicId);
        List<UserTopicRecord> recordList = userTopicRecordRepo.getTopEightOfUserTopic(topic.getId());
        List<UserTopicRankDto> rankDtoList = new ArrayList<>();
        for (int i = 0; i < recordList.size(); i++) {
            UserTopicRankDto rankDto = UserTopicRankDto.builder()
                    .rank(i + 1)
                    .userName(recordList.get(i).getUserTopic().getUserCourse().getUser().getFullName())
                    .userImg(recordList.get(i).getUserTopic().getUserCourse().getUser().getAvatar())
                    .testTime(recordList.get(i).getTestTime())
                    .rightAnswers(recordList.get(i).getRightAnswers())
                    .build();
            rankDtoList.add(rankDto);
        }
        return rankDtoList;
    }


//    Learning Sentence Category

    public List<Sentence> getTopicSentenceToLearn(Long topicId) {
        return sentenceRepo.findByTopics_Id(topicId);
    }

    public List<ContextDto> getContextsBySentence(Long sentenceId) {
        Sentence sentence = isSentenceExist(sentenceId);
        List<Context> contexts = contextRepo.findBySentence_Id(sentence.getId(), Context.class);
        List<ContextDto> contextDtos = contexts.stream().map(context -> modelMapper.map(context, ContextDto.class)).collect(Collectors.toList());

        return renderContextAvatar(contextDtos);
    }

    //Render Anime Avatar for each context after get list of contexts by sentence
    public List<ContextDto> renderContextAvatar(List<ContextDto> contextDtos) {
        Map<Integer, String> person = new HashMap<>();
        int male = 1, female = 1;
        for (ContextDto c : contextDtos) {
            if (person.containsKey(c.getPersonNumber())) {
                c.setImg(person.get(c.getPersonNumber()));
            } else {
                if (c.getGender() == Gender.MALE) {
                    c.setImg("/asset/img/common/male-talk-" + male);
                    male++;
                } else {
                    c.setImg("/asset/img/common/female-talk-" + female);
                    female++;
                }
                person.put(c.getPersonNumber(), c.getImg());
            }
        }
        return contextDtos;
    }

    public UserTopic initialUserTopic(Long topicId, String userId) {
        Topic topic = isTopicExist(topicId);
        UserCourse userCourse = createUserCourse(topic.getCourse().getId(), userId);
        Optional<UserTopic> o_userTopic = userTopicRepo.findByUserCourse_IdAndTopic_Id(userCourse.getId(), topic.getId());
        if (o_userTopic.isEmpty()) {
            UserTopic userTopic = UserTopic.builder()
                    .topic(topic)
                    .userCourse(userCourse)
                    .status(TopicState.PENDING)
                    .build();
            userTopicRepo.save(userTopic);
            return userTopic;
        }
        return o_userTopic.get();
    }


    public void initialUserTopicRecordAtFirstStage(UserTopic userTopic) {//For sentence only
        Optional<UserTopicRecord> o_topicFirst = userTopicRecordRepo.findByStageAndUserTopic_Id(LearningStage.FIRST, userTopic.getId());
        if (o_topicFirst.isEmpty()) {
            UserTopicRecord recordFirst = UserTopicRecord.builder()
                    .rightAnswers(0)
                    .testTime(0)
                    .totalAnswers(0)
                    .stage(LearningStage.FIRST)
                    .userTopic(userTopic)
                    .build();
            userTopicRecordRepo.save(recordFirst);
        } else {
            return;
        }
    }


    public void updateUserTopicSentence(UserTopic userTopic, List<TestSenResultRequest> requests) {
        //Update UserTopicSen
        List<UserTopicSentence> userTopicSentences = userTopicSentenceRepo.findByUserTopic_Id(userTopic.getId());
        if (userTopicSentences.size() < 1) {   //If first time test => create list of UserTopicSentence
            for (TestSenResultRequest s : requests) {
                UserTopicSentence userTopicSentence = modelMapper.map(s, UserTopicSentence.class);
                userTopicSentence.setUserTopic(userTopic);
                userTopicSentenceRepo.save(userTopicSentence);
            }
        } else {//If not first time test => update list status
            if (userTopicSentences.size() == requests.size()) { // If oldList.size == newList.size (in case change number of sentence in a topic ...)
                for (int i = 0; i < userTopicSentences.size(); i++) {
                    modelMapper.map(requests.get(i), userTopicSentences.get(i));
                    userTopicSentenceRepo.save(userTopicSentences.get(i));
                }
            } else if (userTopicSentences.size() > requests.size()) { //If oldList.size > newList.size
                for (int i = 0; i < userTopicSentences.size(); i++) {
                    if (i < requests.size()) {
                        modelMapper.map(requests.get(i), userTopicSentences.get(i));
                        userTopicSentenceRepo.save(userTopicSentences.get(i));
                    } else {
                        userTopicSentenceRepo.delete(userTopicSentences.get(i));
                    }
                }
            } else {
                for (int i = 0; i < requests.size(); i++) { //If oldList.size < newList.size
                    if (i < userTopicSentences.size()) {
                        modelMapper.map(requests.get(i), userTopicSentences.get(i));
                        userTopicSentenceRepo.save(userTopicSentences.get(i));
                    } else {
                        UserTopicSentence userTopicSentence = modelMapper.map(requests.get(i), UserTopicSentence.class);
                        userTopicSentence.setUserTopic(userTopic);
                        userTopicSentenceRepo.save(userTopicSentence);
                    }
                }
            }
        }
    }

    public List<UserTopicSentence> getSenTestResult(Long topicId, String userId) {
        UserTopic userTopic = isUserTopicExist(topicId, userId);
        return userTopicSentenceRepo.findByUserTopic_Id(userTopic.getId());
    }


    //COMMENT SECTION
    public Comments createComment(Long topicId, String userId, CommentRequest request) {
        UserTopic userTopic = isUserTopicExist(topicId, userId);

        Optional<Comments> parentComment = commentsRepo.findByIdAndUserTopic_Topic_Id(request.getIdParent(), topicId);
        if (request.getIdParent() != null && parentComment.isEmpty()) {
            throw new NotFoundException("Không tìm thấy comment có id = " + request.getIdParent() + "trong topic này");
        }

        Comments comment = Comments.builder()
                .message(request.getMessage())
                .createdAt(LocalDateTime.now())
                .idParent(request.getIdParent())
                .userTopic(userTopic)
                .build();

        return commentsRepo.save(comment);

    }

    public List<CommentDto> getAllCommentsByTopic(Long topicId) {
        TimeStampFormat timeStampFormat = new TimeStampFormat();
        Topic topic = isTopicExist(topicId);
        List<Comments> comments = commentsRepo.findByUserTopic_Topic_Id(topic.getId());
        List<CommentDto> commentDtos = comments.stream()
                .map(c -> new CommentDto(
                        c.getId(),
                        timeStampFormat.format(c.getCreatedAt()),
                        c.getMessage(),
                        c.getIdParent(),
                        c.getUserTopic().getUserCourse().getUser().getId(),
                        c.getUserTopic().getUserCourse().getUser().getFullName(),
                        c.getUserTopic().getUserCourse().getUser().getAvatar()))
                .collect(Collectors.toList());

        // sort nested comments
        HashMap<Long, List<CommentDto>> map = new HashMap<>();
        List<CommentDto> parents = new ArrayList<>();
        for (CommentDto c : commentDtos) {
            if (c.getIdParent() == null) {
                parents.add(c);
                List<CommentDto> list = new ArrayList<>();
                map.put(c.getId(), list);
            } else {
                List<CommentDto> list = map.get(c.getIdParent());
                list.add(c);
            }
        }

        List<CommentDto> result = new ArrayList<>();
        for (CommentDto c : parents) {
            result.add(c);
            result.addAll(map.get(c.getId()));
        }
        return result;
    }


    //Check if course is Free course
    public boolean isFreeCourse(Long courseId) {
        Course course = isCourseExist(courseId);
        return course.getGroup().getTitle().equalsIgnoreCase("khóa học miễn phí");
    }

    //Check if topic is the first one in course
    public boolean isFirstTopicInCourse(Topic topic) {
        List<Topic> topics = topicRepo.findByCourse_IdOrderByIdAsc(topic.getCourse().getId());
        return topics.get(0).getId() == topic.getId();
    }

    //Check if topic can be accessed by USER_NORMAL
    public boolean topicCanBeAccessedByNormalUser(Topic topic) {
        if (isFreeCourse(topic.getCourse().getId())) {
            return true;
        }
        return isFirstTopicInCourse(topic);
    }

    //Check is Topic open for user yet: means user pass previous topic
    public boolean isTopicOpenForUser(Topic topic, String userId) {
        if (!isFirstTopicInCourse(topic)) {   //only from second topic
            Optional<UserTopic> o_userTopic = userTopicRepo.findByTopic_IdAndUserCourse_User_Id(topic.getId(), userId, UserTopic.class);
            return o_userTopic.isPresent(); // if userTopic exist: topic already open
        }
        return true;
    }

    public boolean isNormalUser() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .filter(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER_NORMAL"))
                .findFirst()
                .isPresent();
    }

    public boolean generalCheckUserAccessToTopic(Long topicId, String userId) {
        Topic topic = isTopicExist(topicId);

        return (!isNormalUser() || topicCanBeAccessedByNormalUser(topic))
                && isTopicOpenForUser(topic, userId);
    }


//    Helper Class

    public Topic isTopicExist(Long topicId) {
        Optional<Topic> o_topic = topicRepo.findTopicById(topicId, Topic.class);
        if (!o_topic.isPresent()) {
            throw new BadRequestException("Không tồn tại topic có Id = " + topicId);
        }
        return o_topic.get();
    }

    public Users isUserExist(String userId) {
        Optional<Users> o_user = usersRepo.findById(userId);
        if (!o_user.isPresent()) {
            throw new BadRequestException("Không tồn tại user có Id = " + userId);
        }
        return o_user.get();
    }

    public Course isCourseExist(Long courseId) {
        Optional<Course> o_course = courseRepo.findCourseById(courseId);
        if (!o_course.isPresent()) {
            throw new BadRequestException("Không tồn tại course có Id = " + courseId);
        }
        return o_course.get();
    }

    public Vocab isVocabExist(Long vocabId) {
        Optional<Vocab> o_vocab = vocabRepo.findById(vocabId);
        if (o_vocab.isEmpty()) {
            throw new BadRequestException("Không tồn tại từ vựng có Id = " + vocabId);
        }
        return o_vocab.get();
    }

    public UserTopic isUserTopicExist(Long topicId, String userId) {
        Topic topic = isTopicExist(topicId);
        Users user = isUserExist(userId);
        Optional<UserTopic> o_userTopic = userTopicRepo.findByTopic_IdAndUserCourse_User_Id(topic.getId(), user.getId(), UserTopic.class);
        if (o_userTopic.isEmpty()) {
            throw new BadRequestException("Không tìm thấy UserTopic");
        }
        return o_userTopic.get();
    }

    public Sentence isSentenceExist(Long sentenceId) {
        Optional<Sentence> o_sentence = sentenceRepo.findById(sentenceId);
        if (o_sentence.isEmpty()) {
            throw new NotFoundException("Không tìm thấy sentence có id = " + sentenceId);
        }
        return o_sentence.get();
    }


}
