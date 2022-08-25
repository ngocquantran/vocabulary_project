package com.example.myvocab.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserInfoRequest {
    private String fullName;
    private String phone;
    private MultipartFile file;
}
