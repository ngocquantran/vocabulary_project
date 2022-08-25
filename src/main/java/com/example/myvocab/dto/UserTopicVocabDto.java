package com.example.myvocab.dto;

import com.example.myvocab.model.UserTopic;
import com.example.myvocab.model.Vocab;
import com.example.myvocab.model.enummodel.LearningStage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include= JsonSerialize.Inclusion.NON_EMPTY)
public class UserTopicVocabDto {
    private Long id;

    @JsonProperty
    private boolean status;
    private LearningStage learningStage;
    private Integer testTime;

    @JsonIgnore
    private UserTopic userTopic;
    private Vocab vocab;

    @JsonProperty
    private boolean learn;
}