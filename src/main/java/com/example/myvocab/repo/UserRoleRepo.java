package com.example.myvocab.repo;

import com.example.myvocab.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepo extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByRole_IdAndUser_Id(Long roleId, String userId);

}