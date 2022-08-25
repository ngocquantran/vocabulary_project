package com.example.myvocab.dto;

public interface VocabTestResultDto {
    Long getId();

    boolean isStatus();

    VocabInfo getVocab();

    interface VocabInfo {
        Long getId();

        String getAudio();

        String getImg();

        String getPhonetic();

        String getType();

        String getVnMeaning();

        String getWord();
    }
}
