package com.example.myvocab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChooseVocabDto {
    private Long vocabId;
    private String word;
    private String type;
    private String img;
    private boolean status;
}
