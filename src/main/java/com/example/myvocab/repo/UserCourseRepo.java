package com.example.myvocab.repo;

import com.example.myvocab.model.UserCourse;
import com.example.myvocab.dto.UserCourseInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserCourseRepo extends JpaRepository<UserCourse, Long> {
    Optional<UserCourse> findByCourse_IdAndUser_Id(Long id, String id1);

    List<UserCourseInfo> findByUser_IdOrderByStudiedAtDesc(String id);

    List<UserCourseInfo> findByUser_IdAndCourse_Category_IdOrderByStudiedAtDesc(String userId, Long categoryId);


    boolean existsByCourse_Id(Long id);
















}