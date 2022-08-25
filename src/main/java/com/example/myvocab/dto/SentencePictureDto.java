package com.example.myvocab.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SentencePictureDto {
    private Long id;
    private String content;
    private String img;
}
