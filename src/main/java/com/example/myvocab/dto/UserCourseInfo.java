package com.example.myvocab.dto;

import com.example.myvocab.model.CourseCategory;

import java.time.LocalDate;

public interface UserCourseInfo {
    Long getId();

    Integer getFailedSens();

    Integer getFailedVocabs();

    Integer getFinishedTopics();

    Integer getPassedSens();

    Integer getPassedVocabs();

    LocalDate getStudiedAt();

    CourseInfo getCourse();

    interface CourseInfo {
        Long getId();

        CourseCategory getCategory();


        String getThumbnail();

        String getTitle();

        String getNumberOfTopics();
    }
}
