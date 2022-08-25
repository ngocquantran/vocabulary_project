package com.example.myvocab.repo;

import com.example.myvocab.model.Context;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContextRepo extends JpaRepository<Context, Long> {
    <T> List<T> findBySentence_Id(Long id, Class<T> type);

}