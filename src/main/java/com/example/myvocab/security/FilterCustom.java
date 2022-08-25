package com.example.myvocab.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class FilterCustom extends OncePerRequestFilter {

    @Autowired
    private UserDetailsServiceCustom userDetailsServiceCustom;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //Lấy thông tin username (email) trong session
        String userEmail = (String) request.getSession().getAttribute("MY_SESSION");

        //Tạo đối tượng xác thực
        UsernamePasswordAuthenticationToken authentication = getAuthentication(userEmail);
        if (authentication == null) {
            filterChain.doFilter(request, response);
            return;
        }

        //Lưu vào trong context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String email) {
        if (email == null) {
            return null;
        }
        //Lấy ra thông tin của user theo email
        UserDetails userDetails = userDetailsServiceCustom.loadUserByUsername(email);

        //Tạo đối tượng xác thực
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
