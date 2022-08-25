package com.example.myvocab.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include= JsonSerialize.Inclusion.NON_EMPTY)
public class VocabAddRequest {
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
}
