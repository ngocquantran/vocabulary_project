package com.example.myvocab.model.enummodel;

public enum UserState {
    PENDING("PENDING"),ACTIVE("ACTIVE"),DISABLED("DISABLED");
    private String code;

    UserState(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
