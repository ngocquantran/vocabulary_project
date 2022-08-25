package com.example.myvocab.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user_course_game")
public class UserCourseGame {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int testTime;

    @ManyToOne
    @JoinColumn(name = "id_user_course", referencedColumnName = "id", nullable = false)
    private UserCourse userCourse;

}
