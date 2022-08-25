package com.example.myvocab.util;

import java.util.regex.Pattern;

public class Validation {

    public static boolean isValidEmail(String email){
        String pattern="^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.compile(pattern)
                .matcher(email)
                .matches();
    }

    public static boolean isValidPassword(String password){
        return password.length()>=8 && password.length()<=16;
    }
}
