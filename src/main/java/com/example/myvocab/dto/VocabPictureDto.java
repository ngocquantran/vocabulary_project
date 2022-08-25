package com.example.myvocab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VocabPictureDto {
    private Long id;
    private String word;
    private String type;
    private String vnMeaning;
    private String img;
}
