package com.example.myvocab.mapper;

import com.example.myvocab.dto.VocabTestDto;
import com.example.myvocab.model.Vocab;


public class VocabTestMapper {
    public static VocabTestDto toVocabTest(Vocab vocab){
        return new VocabTestDto(vocab.getId(),vocab.getWord(), vocab.getImg(), vocab.getType(), vocab.getAudio(),vocab.getPhonetic(),vocab.getEnMeaning(),vocab.getVnMeaning(),vocab.getEnSentence(),vocab.getVnSentence(),vocab.getSenAudio());
    }
}
