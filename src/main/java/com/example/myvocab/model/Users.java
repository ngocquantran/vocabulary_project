package com.example.myvocab.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class Users implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "VARCHAR(255)")
    private String id;
    private String avatar;
    private String fullName;

    @Column(unique = true)
    private String email;

    private String password;
    private LocalDate birth;
    private String phone;
    private LocalDate startDate;

    @Column(name = "verification_code", length = 64)
    private String verificationCode;
    private boolean enabled;


    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER)
    private List<UserRole> userRoles = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        startDate=LocalDate.now();
    }




    public void addUserRole(UserRole userRole){
        this.userRoles.add(userRole);
    }


}
