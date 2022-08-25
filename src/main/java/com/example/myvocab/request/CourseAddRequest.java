package com.example.myvocab.request;

import com.example.myvocab.model.Levels;
import com.example.myvocab.model.enummodel.CourseStatus;
import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;


import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CourseAddRequest {

    private String title;
    private String description;
    private String content;
    private String targetLearner;
    private String goal;
    private String thumbnail;
    private CourseStatus status;
    private Long groupId;
    private Long  categoryId;
    private List<Long> levels=new ArrayList<>();
    private List<Long> topics=new ArrayList<>();

}
