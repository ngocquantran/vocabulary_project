package com.example.myvocab.repo;

import com.example.myvocab.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepo extends JpaRepository<Roles, Long> {
    Optional<Roles> findByName(String name);


}