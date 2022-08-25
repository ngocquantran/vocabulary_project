package com.example.myvocab.model;

import com.example.myvocab.model.enummodel.RoleUser;
import com.example.myvocab.model.enummodel.UserState;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_role")
public class UserRole  implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate startDate;
    private LocalDate dueDate;
    private UserState status;

    @ManyToOne()
    @JoinColumn(name = "id_role", referencedColumnName = "id", nullable = false)
    private Roles role;

    @ManyToOne()
    @JoinColumn(name = "id_user", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Users user;


    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_package", referencedColumnName = "id")
    private Package packages;



    @PrePersist
    public void prePersist() {
        startDate = LocalDate.now();
    }
}
