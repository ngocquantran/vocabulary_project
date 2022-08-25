package com.example.myvocab.model.enummodel;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<Gender,String> {
    @Override
    public String convertToDatabaseColumn(Gender gender) {
        if (gender==null){
            return null;
        }
        return gender.getCode();
    }

    @Override
    public Gender convertToEntityAttribute(String s) {
        if (s==null){
            return null;
        }
        return Stream.of(Gender.values()).filter(gender -> gender.getCode().equals(s))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
