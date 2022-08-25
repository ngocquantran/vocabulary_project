package com.example.myvocab.model.enummodel;


public enum LearningStage {
    FIRST("FIRST"),BEST("BEST"),PREVIOUS("PREVIOUS"),NOW("NOW");
    private String code;

    LearningStage(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
