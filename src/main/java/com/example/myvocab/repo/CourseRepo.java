package com.example.myvocab.repo;

import com.example.myvocab.model.Course;
import com.example.myvocab.model.CourseCategory;
import com.example.myvocab.model.CourseGroup;
import com.example.myvocab.model.enummodel.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepo extends JpaRepository<Course, Long> , JpaSpecificationExecutor<Course> {

    @Query("select c from Course c where upper(c.category.title) = upper(?1)")
    List<Course> findCoursesByCategory_TitleEqualsIgnoreCase(String category);

    List<Course> findCoursesByCategory(CourseCategory category);

    List<Course> findByStatus(CourseStatus status);



    List<Course> findCoursesByGroup(CourseGroup group);

    List<Course> findByCategory_Id(Long id);

    Optional<Course> findCourseById(Long id);

    @Query("select c from Course c INNER JOIN Topic t ON t.course=c WHERE t.id=:id")
    Optional<Course> findByTopicId(Long id);


    @Query("select c.group from Course c where upper(c.category.title) = upper(?1) group by c.group")
    List<CourseGroup> getGroupsByCategory(String category);

    @Query("select c.group from Course as c where c.category.id=?1 group by c.group")
    List<CourseGroup> getGroupByCourseCategory(Long categoryId);

    List<Course> findByCategory_IdAndGroup_IdAndStatus(Long categoryId, Long groupId, CourseStatus status);




    @Query(value = "SELECT c FROM Course c left join c.levels l where concat(c.title,' ',c.category.title, ' ', l.title,' ' ,c.group.title) like %?1% group by c")
    Page<Course> listCourseByKeyWord(String keyword, Pageable pageable);







}