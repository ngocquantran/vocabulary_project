package com.example.myvocab.controller;

import com.example.myvocab.model.Users;
import com.example.myvocab.request.LoginRequest;
import com.example.myvocab.request.SignUpRequest;
import com.example.myvocab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;


@RestController
public class AuthApi {
    @Autowired
    private
    AuthenticationManager authManager;

    @Autowired
    private UserService userService;

    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        try {
            userService.checkExpiredUserRole(request.getEmail());

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
            Authentication authentication = authManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            session.setAttribute("MY_SESSION", authentication.getName());
            return ResponseEntity.ok().body(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }

    @PostMapping("/api/auth/signup")
    public void Signup(@RequestBody SignUpRequest request, HttpServletRequest httpRequest) throws UnsupportedEncodingException, MessagingException {   //Tạo User role USER_NORMAL
        userService.register(request, getSiteURL(httpRequest));
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    @PostMapping("/api/forgot-password")
    public ResponseEntity<?> ResetUserPassword(@RequestParam(value = "email") String email) throws MessagingException, UnsupportedEncodingException {
        System.out.println(email);
        userService.resetUserPassword(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
