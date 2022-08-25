package com.example.myvocab.dto;

import com.example.myvocab.model.CourseCategory;
import com.example.myvocab.model.enummodel.CourseStatus;

public interface TopicToCourseDto {
    Long getId();

    String getTitle();

    CourseInfo getCourse();

    interface CourseInfo {
        Long getId();
        CourseCategory getCategory();
        CourseStatus getStatus();
    }
}
