package com.example.myvocab.repo;

import com.example.myvocab.model.CourseGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseGroupRepo extends JpaRepository<CourseGroup, Long> {



}