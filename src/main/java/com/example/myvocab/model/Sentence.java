package com.example.myvocab.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "sentence")
public class Sentence {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private String img;
    private String phonetic;
    private String vnSentence;
    private String senAudio;
    private String wordsAudio;
    private String wordsTimestamp;
    private String apply;
    private String enContextDesc;
    private String vnContextDesc;
    private String contextAudio;


    @ManyToMany(mappedBy = "sentences",fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JsonIgnore
    private Set<Topic> topics=new HashSet<>();

    public void addTopic(Topic topic){
        this.topics.add(topic);
    }


}
