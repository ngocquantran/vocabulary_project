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
@Table(name = "vocab")
public class Vocab {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String word;
    private String img;
    private String type;
    private String audio;
    private String phonetic;
    private String enMeaning;
    private String vnMeaning;
    private String enSentence;
    private String vnSentence;
    private String senAudio;


    @ManyToMany(mappedBy = "vocabs",fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JsonIgnore
    private Set<Topic> topics=new HashSet<>();



}
