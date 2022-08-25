package com.example.myvocab.model.enummodel;

public enum CourseStatus {
    PRIVATE("PRIVATE"), PUBLIC("PUBLIC");

    private String code;

    CourseStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
