package com.example.myvocab.model.enummodel;


import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class CourseStatusConverter implements AttributeConverter<CourseStatus,String> {
    @Override
    public String convertToDatabaseColumn(CourseStatus courseStatus) {
        if (courseStatus==null){
            return null;
        }
        return courseStatus.getCode();
    }

    @Override
    public CourseStatus convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(CourseStatus.values()).filter(courseStatus -> courseStatus.getCode().equals(code)).findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
