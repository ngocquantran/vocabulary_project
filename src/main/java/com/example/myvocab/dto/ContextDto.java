package com.example.myvocab.dto;

import com.example.myvocab.model.enummodel.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class ContextDto {
    private Long id;
    private int personNumber;
    private String name;
    private Gender gender;
    private String enSentence;
    private String vnSentence;
    private boolean isContainKey;
    private int startTime;
    private int endTime;
    private String img;

    public ContextDto(){
        this.img="";
    }
}
