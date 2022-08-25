package com.example.myvocab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Random;

@Data
@AllArgsConstructor
@Builder
public class VocabTestDto {
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

    private int answerIndex;
    private List<String> vocabs;
    private List<String> vnMeanings;
    private List<String> enSentences;

    public VocabTestDto(Long id, String word, String img, String type, String audio, String phonetic, String enMeaning, String vnMeaning, String enSentence, String vnSentence, String senAudio) {
        this.id = id;
        this.word = word;
        this.img = img;
        this.type = type;
        this.audio = audio;
        this.phonetic = phonetic;
        this.enMeaning = enMeaning;
        this.vnMeaning = vnMeaning;
        this.enSentence = enSentence;
        this.vnSentence = vnSentence;
        this.senAudio = senAudio;
        Random rd=new Random();
        this.answerIndex = rd.nextInt(4)+1;
    }
}
