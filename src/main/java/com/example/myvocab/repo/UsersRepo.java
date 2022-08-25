package com.example.myvocab.repo;

import com.example.myvocab.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepo extends JpaRepository<Users, String> {
    Optional<Users> findByEmail(String email);


    @Query(value = "SELECT u FROM Users u left join u.userRoles ur  where concat(u.email,' ',u.fullName, ' ',ur.role.name ) like %?1% group by u")
    Page<Users> listUsersByKeyWord(String keyword, Pageable pageable);

    Users findByVerificationCode(String verificationCode);







}