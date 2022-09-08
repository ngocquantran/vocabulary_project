package com.example.myvocab.model;

import com.example.myvocab.model.enummodel.CourseStatus;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

//@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "course")
public class Course {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String content;

    @Basic
    @Column(name = "targetLearner")
    private String targetLearner;


    private String goal;
    private String thumbnail;

    private CourseStatus status;


    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "id_group", referencedColumnName = "id", nullable = false)
    private CourseGroup group;


    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "id_category", referencedColumnName = "id", nullable = false)
    private CourseCategory category;


    @ManyToMany(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(
            name = "course_level",
            joinColumns = @JoinColumn(name = "id_course"),
            inverseJoinColumns = @JoinColumn(name = "id_level")
    )
    private List<Levels> levels=new ArrayList<>();

    @PreRemove
    private void removeLevels(){
        for (Levels l:levels){
            l.getCourses().remove(this);
        }
    }


    @Formula("(SELECT COUNT(*) FROM topic t WHERE t.id_course = id)")
    private int numberOfTopics;

    public List<String> getLevelTitles(){
        return levels.stream().map(Levels::getTitle).collect(Collectors.toList());
    }

//    @OneToMany(mappedBy = "course", orphanRemoval = true)
//    private List<Topic> topics = new ArrayList<>();

//    @OneToMany(mappedBy = "course")
//    private Set<UserCourse> userCourses=new HashSet<>();


}
