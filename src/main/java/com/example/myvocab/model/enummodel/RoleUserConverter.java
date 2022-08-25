package com.example.myvocab.model.enummodel;

import javax.management.relation.Role;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class RoleUserConverter implements AttributeConverter<RoleUser,String> {
    @Override
    public String convertToDatabaseColumn(RoleUser roleUser) {
        if (roleUser==null){
            return null;
        }
        return roleUser.getCode();
    }

    @Override
    public RoleUser convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(RoleUser.values()).filter(r -> r.getCode().equals(code)).findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
