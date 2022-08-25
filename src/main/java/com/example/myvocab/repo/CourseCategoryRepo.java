package com.example.myvocab.repo;

import com.example.myvocab.model.CourseCategory;
import jdk.jfr.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseCategoryRepo extends JpaRepository<CourseCategory, Long> {
    Optional<CourseCategory> findByTitleEqualsIgnoreCase(String title);



}