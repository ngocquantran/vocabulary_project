package com.example.myvocab.model;

import com.example.myvocab.model.enummodel.LearningStage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "user_topic_vocab")
public class UserTopicVocab {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "boolean default false")
    private boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_topic")
    @JsonIgnore
    private UserTopic userTopic;

    @ManyToOne
    @JoinColumn(name = "id_vocab")
    private Vocab vocab;

    @Column(columnDefinition = "boolean default false")
    private boolean learn;



}
