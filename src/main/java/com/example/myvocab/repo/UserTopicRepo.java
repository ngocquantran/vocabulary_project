package com.example.myvocab.repo;

import com.example.myvocab.model.UserTopic;
import com.example.myvocab.model.enummodel.TopicState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTopicRepo extends JpaRepository<UserTopic, Long> {
    List<UserTopic> findByUserCourse_Course_IdAndUserCourse_User_Id(Long id, String id1);

    <T> Optional<T> findByTopic_IdAndUserCourse_User_Id(Long id, String id1, Class<T> type);

    Optional<UserTopic>  findByTopic_IdAndUserCourse_User_IdAndStatus(Long id, String id1, TopicState status);

    List<UserTopic> findByUserCourse_Id(Long id);

    Optional<UserTopic> findByUserCourse_IdAndTopic_Id(Long id, Long id1);

    List<UserTopic> findByUserCourse_User_Id(String id);



    
    


}