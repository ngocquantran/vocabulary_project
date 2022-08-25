package com.example.myvocab.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserOrderInfo {
    private String fullName;
    private String phone;
    private String email;
}
