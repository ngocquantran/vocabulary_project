package com.example.myvocab.dto;

import com.example.myvocab.model.Package;
import com.example.myvocab.model.Roles;
import com.example.myvocab.model.Users;
import com.example.myvocab.model.enummodel.UserState;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;


public interface UsersInfo {
    String getId();

    String getAvatar();

    LocalDate getBirth();

    String getEmail();

    String getFullName();

    String getPhone();

    LocalDate getStartDate();

    Set<UserRoleInfo> getUserRoles();

    interface UserRoleInfo {
        Long getId();

        LocalDate getDueDate();

        Package getPackages();

        Roles getRole();

        LocalDate getStartDate();

        UserState getStatus();

    }
}
