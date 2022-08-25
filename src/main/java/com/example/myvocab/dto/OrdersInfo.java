package com.example.myvocab.dto;

import com.example.myvocab.model.enummodel.OrderStatus;

import java.time.LocalDate;

public interface OrdersInfo {
    Long getId();

    LocalDate getActiveDate();

    LocalDate getOrderDate();

    OrderStatus getStatus();

    PackageInfo getaPackage();

    UsersInfo getUser();

    interface PackageInfo {
        Long getId();

        String getDescription();

        int getDuration();

        Long getPricePerMonth();

        String getTitle();

        String getType();
    }

    interface UsersInfo {
        String getEmail();

        String getFullName();

        String getPhone();
    }
}
