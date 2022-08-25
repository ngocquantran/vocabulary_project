package com.example.myvocab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageDto {
    private int currentPage;
    private long totalElements;
    private int totalPages;
    private List dataList=new ArrayList();

}
