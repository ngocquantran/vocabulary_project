package com.example.myvocab.dto;

import com.example.myvocab.model.CourseCategory;

import javax.persistence.Transient;

public interface TopicHaveCourseDto {
    Long getId();

    String getTitle();

    CourseInfo getCourse();

    int getNumberOfVocabs();

    int getNumberOfSens();


    interface CourseInfo {
        Long getId();

        CourseCategory getCategory();

        String getTitle();
    }
}
