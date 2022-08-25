package com.example.myvocab.repo;

import com.example.myvocab.dto.TopicHaveCourseDto;
import com.example.myvocab.model.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepo extends JpaRepository<Topic, Integer> {

    List<Topic> findByCourse_IdOrderByIdAsc(Long id);



    <T> Optional<T> findTopicById(Long id, Class<T> type);

    @Query(value = "SELECT t FROM Topic t")
    Page<TopicHaveCourseDto> findAllTopic(Pageable pageable);

    @Query(value = "SELECT t FROM Topic t where t.title like %?1% or t.course.category.title like %?1% or t.course.title like %?1% ")
    Page<TopicHaveCourseDto> listTopicsByKeyWord(String keyword, Pageable pageable);

    @Query("select t from Topic t where t.course is null ")
    List<TopicHaveCourseDto> getTopicsWithNoCourse();

//    @Query(value = "SELECT t FROM Topic t where concat(t.title,' ',t.course.category.title, ' ',t.course.title) like %?1% ")
//    Page<TopicHaveCourseDto> listTopicsByKeyWord(String keyword, Pageable pageable);




}