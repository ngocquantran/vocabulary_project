package com.example.myvocab.model.enummodel;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class UserStateConverter implements AttributeConverter<UserState,String> {

    @Override
    public String convertToDatabaseColumn(UserState userState) {
        if (userState==null){
            return null;
        }
        return userState.getCode();
    }

    @Override
    public UserState convertToEntityAttribute(String code) {
        if (code==null){
            return null;
        }
        return Stream.of(UserState.values()).filter(u -> u.getCode().equals(code)).findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
