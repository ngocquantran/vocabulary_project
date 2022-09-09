package com.example.myvocab.dto;

import com.example.myvocab.model.CourseCategory;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface UserCourseInfo {
    Long getId();

    Integer getFailedSens();

    Integer getFailedVocabs();

    Integer getFinishedTopics();

    Integer getPassedSens();

    Integer getPassedVocabs();

    LocalDateTime getStudiedAt();

    CourseInfo getCourse();

    interface CourseInfo {
        Long getId();

        CourseCategory getCategory();


        String getThumbnail();

        String getTitle();

        String getNumberOfTopics();
    }
}
