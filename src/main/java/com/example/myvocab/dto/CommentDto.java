package com.example.myvocab.dto;


import lombok.*;
import org.joda.time.DateTime;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CommentDto {
    private Long id;
    private String createdAt;
    private String message;
    private Long idParent;
    private String userId;
    private String userName;
    private String userAvatar;

}
