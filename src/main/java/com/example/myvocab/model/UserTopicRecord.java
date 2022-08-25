package com.example.myvocab.model;

import com.example.myvocab.model.enummodel.LearningStage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_topic_record")
public class UserTopicRecord {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer testTime;
    private Integer rightAnswers;
    private Integer totalAnswers;
    private LearningStage stage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_topic")
    @JsonIgnore
    private UserTopic userTopic;


}
