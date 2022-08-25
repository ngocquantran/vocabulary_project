package com.example.myvocab.repo;

import com.example.myvocab.dto.FilterVocabDto;
import com.example.myvocab.model.Course;
import com.example.myvocab.model.Vocab;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabRepo extends JpaRepository<Vocab, Long> {
    @Query("select v from Vocab v left join v.topics topics where topics.id = ?1")
    List<Vocab> findByTopics_Id(Long id);

    List<Vocab> findByTopics_Course_Id(Long id);

    @Query("SELECT new com.example.myvocab.dto.FilterVocabDto(v.id,v.word,v.type,v.audio) "+
            "from Vocab v left join v.topics t "+
            "WHERE t.id=:topicId")
    List<FilterVocabDto> getVocabsToFilter(Long topicId);

    @Query(value = "SELECT v FROM Vocab v where concat(v.word,' ',v.type, ' ', v.phonetic, ' ',v.vnMeaning) like %?1%")
    Page<Vocab> listVocabByKeyWord(String keyword, Pageable pageable);

}