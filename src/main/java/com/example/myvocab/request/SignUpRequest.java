package com.example.myvocab.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpRequest {
    private String fullName;
    private String email;
    private String password;
}
