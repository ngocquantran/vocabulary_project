package com.example.myvocab.model;

import com.example.myvocab.model.enummodel.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "context")
public class Context {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int personNumber;
    private String name;
    private Gender gender;
    private String enSentence;
    private String vnSentence;
    private boolean isContainKey;
    private int startTime;
    private int endTime;

    @ManyToOne
    @JoinColumn(name = "id_sentence")
    @JsonIgnore
    private Sentence sentence;


}
