package com.example.myvocab.controller;


import com.example.myvocab.model.Orders;
import com.example.myvocab.model.Users;
import com.example.myvocab.repo.UsersRepo;
import com.example.myvocab.request.SignUpRequest;
import com.example.myvocab.request.UpdatePasswordRequest;
import com.example.myvocab.request.UpdateUserInfoRequest;
import com.example.myvocab.security.UserDetailsCustom;
import com.example.myvocab.service.UserService;
import com.example.myvocab.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
public class UserApiController {

    @Autowired
    private UserService userService;
    @Autowired
    private UsersRepo usersRepo;

    @PostMapping("/api/user/updatePassword")
    @PreAuthorize("hasAnyRole('USER_NORMAL','USER_VIP')")
    public void updatePassword(@RequestBody UpdatePasswordRequest request) {
        Users user = ((UserDetailsCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        userService.handleChangeUserPassword(user, request);

    }

    @PostMapping("/api/user/updateAvatar")
    public void saveAvatar(@ModelAttribute("file") MultipartFile file) throws IOException {
        Users user = ((UserDetailsCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        if (file == null) {
            System.out.println("Không có file");
        } else {
            userService.handleUploadUserAvatar(user, file);
        }
    }

    @PostMapping("/api/user/updateUserInfo")
    public void updateUserInfo(@ModelAttribute("file") MultipartFile file, @ModelAttribute("fullName") String fullName, @ModelAttribute("phone") String phone) throws IOException {
        Users user = ((UserDetailsCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        if (file == null) {
            userService.updateUserInfo(phone, fullName, user);
        } else {
            userService.updateUserInfo(phone, fullName, user);
            userService.handleUploadUserAvatar(user, file);
        }


    }

    @PreAuthorize("hasRole('USER_NORMAL')")
    @PostMapping("/api/user/order")
    public Long submitOrder(@RequestParam("id") Long idPackage) {
        Users user = usersRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        Orders order = userService.createPendingOrder(user, idPackage);
        return order.getId();
    }

}
