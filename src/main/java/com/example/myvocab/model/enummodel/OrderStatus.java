package com.example.myvocab.model.enummodel;



public enum OrderStatus {
    PENDING("PENDING"), ACTIVATED("ACTIVATED"),EXPIRED("EXPIRED");

    private String code;

    OrderStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
