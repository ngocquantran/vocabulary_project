package com.example.myvocab.security;

import com.example.myvocab.model.Users;

import com.example.myvocab.repo.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class UserDetailsServiceCustom implements UserDetailsService {

    @Autowired
    private UsersRepo usersRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> o_user = usersRepo.findByEmail(username);
        if (o_user.isPresent()) {

            return new UserDetailsCustom(o_user.get());
        }
        throw new UsernameNotFoundException("User " + username + " not found");
    }


}
