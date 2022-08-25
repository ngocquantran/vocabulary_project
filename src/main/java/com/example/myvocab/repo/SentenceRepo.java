package com.example.myvocab.repo;

import com.example.myvocab.model.Sentence;
import com.example.myvocab.model.Vocab;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SentenceRepo extends JpaRepository<Sentence, Long> {
    List<Sentence> findByTopics_Id(Long id);

    @Query(value = "SELECT s FROM Sentence s where concat(s.content,' ',s.phonetic, ' ', s.vnSentence) like %?1%")
    Page<Sentence> listSentenceByKeyWord(String keyword, Pageable pageable);


}