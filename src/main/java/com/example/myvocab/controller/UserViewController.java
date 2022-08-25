package com.example.myvocab.controller;


import com.example.myvocab.dto.OrdersInfo;
import com.example.myvocab.dto.UserPersonalDto;
import com.example.myvocab.dto.UserCourseInfo;
import com.example.myvocab.model.Orders;
import com.example.myvocab.model.UserTopic;
import com.example.myvocab.model.Users;
import com.example.myvocab.model.enummodel.OrderStatus;
import com.example.myvocab.security.UserDetailsCustom;
import com.example.myvocab.service.UserService;
import com.example.myvocab.service.ViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserViewController {
    @Autowired
    private UserService userService;

    @Autowired
    private ViewService viewService;

    @GetMapping("/user/personal")
    public String getPersonalPage(@AuthenticationPrincipal UserDetailsCustom userDetailsCustom, Model model) {
        UserPersonalDto usersInfo = userService.getUserInfoFromUserCustom(userDetailsCustom);
        model.addAttribute("user", usersInfo);

        List<UserTopic> userTopicList = userService.getUserTopicsByUser(userDetailsCustom.getUser().getId());
        model.addAttribute("numberOfTopics", userTopicList.size());

        List<UserCourseInfo> userCourses = userService.getUserCourseByUser(userDetailsCustom.getUser().getId());
        model.addAttribute("userCourses", userCourses);

        return "web/personal";
    }


    @GetMapping("/user/profile")
    public String getProfilePage(@AuthenticationPrincipal UserDetailsCustom userDetailsCustom, Model model) {
        UserPersonalDto usersInfo = userService.getUserInfoFromUserCustom(userDetailsCustom);
        model.addAttribute("user", usersInfo);
        return "web/profile";
    }

    @GetMapping("/user/license")
    public String getLicensePage(Model model) {
        Users user = ((UserDetailsCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        List<OrdersInfo> ordersInfos = viewService.getAllOrderByUser(user.getId());
        model.addAttribute("orders", ordersInfos);

        List<OrdersInfo> activeOrders = viewService.getActiveOrderByUser(user.getId(), OrderStatus.ACTIVATED);
        model.addAttribute("activeOrders", activeOrders);

        return "web/license";
    }

    @GetMapping("/user/payment")
    public String getPaymentPage(Model model, @RequestParam("id") Long idOrder) {

        OrdersInfo order = viewService.getOrderById(idOrder);
        model.addAttribute("order", order);

        return "web/payment";
    }

    @GetMapping("/verify")
    public String verifyUser(@RequestParam(value = "code") String code) {
        if (userService.verifySignUp(code)) {
            return "web/verify-success";
        } else
            return "web/verify-fail";
    }
}
