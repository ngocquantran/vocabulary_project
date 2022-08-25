package com.example.myvocab.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicAddRequest {
    private String img;
    private String type;
    private String title;
    private List<Long>  content;
}
