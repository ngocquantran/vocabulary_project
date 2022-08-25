package com.example.myvocab.repo;

import com.example.myvocab.dto.ChooseVocabDto;
import com.example.myvocab.dto.VocabTestResultDto;
import com.example.myvocab.model.UserTopicVocab;
import com.example.myvocab.model.Vocab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTopicVocabRepo extends JpaRepository<UserTopicVocab, Long> {
    Optional<UserTopicVocab> findByUserTopic_IdAndVocab_Id(Long userTopicId, Long vocabId);

    @Query("select new com.example.myvocab.dto.ChooseVocabDto(u.vocab.id,u.vocab.word,u.vocab.type,u.vocab.img,u.status) from UserTopicVocab u where u.userTopic.id = ?1")
    List<ChooseVocabDto> getTopicVocabsToChoose(Long userTopicId);

    @Query("select u.vocab from UserTopicVocab u where u.userTopic.id = ?1 and u.learn = ?2")
    List<Vocab> findByUserTopic_IdAndLearn(Long id, boolean learn);

    List<VocabTestResultDto> findByUserTopic_Id(Long id);


    boolean existsByUserTopic_IdAndVocab_Id(Long topicId, Long vocabId);

    long countDistinctByUserTopic_Id(Long id);









}