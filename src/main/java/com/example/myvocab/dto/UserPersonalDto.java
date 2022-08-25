package com.example.myvocab.dto;

import com.example.myvocab.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserPersonalDto {
    private String id;
    private String avatar;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate birth;
    private LocalDate startDate;
    private Set<UserRole> userRoles = new HashSet<>();
}
