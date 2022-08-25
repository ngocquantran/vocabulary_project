package com.example.myvocab.model.enummodel;

public enum RoleUser {
    ADMIN("ADMIN"),USER_NORMAL("USER_NORMAL"),USER_VIP("USER_VIP");
    private String code;

    RoleUser(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
