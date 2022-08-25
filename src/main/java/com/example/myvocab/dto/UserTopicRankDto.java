package com.example.myvocab.dto;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserTopicRankDto {
    private Integer rank;
    private String userName;
    private String userId;
    private String userImg;
    private Integer rightAnswers;
    private Integer testTime;
}
