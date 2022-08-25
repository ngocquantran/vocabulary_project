package com.example.myvocab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterVocabDto {
    private Long id;
    private String word;
    private String type;
    private String audio;
}
