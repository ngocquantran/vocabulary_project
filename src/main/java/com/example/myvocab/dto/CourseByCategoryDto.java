package com.example.myvocab.dto;

import com.example.myvocab.model.Course;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
public class CourseByCategoryDto {
    private Long id;
    private String title;
    private List<Course> courses;
}
