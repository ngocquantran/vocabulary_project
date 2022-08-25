package com.example.myvocab.repo;

import com.example.myvocab.model.UserTopicSentence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTopicSentenceRepo extends JpaRepository<UserTopicSentence, Long> {
    List<UserTopicSentence> findByUserTopic_Id(Long id);

}