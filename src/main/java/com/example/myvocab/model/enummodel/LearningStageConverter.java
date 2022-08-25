package com.example.myvocab.model.enummodel;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class LearningStageConverter implements AttributeConverter<LearningStage,String> {
    @Override
    public String convertToDatabaseColumn(LearningStage learningStage) {
        if (learningStage==null){
            return null;
        }
        return learningStage.getCode();
    }

    @Override
    public LearningStage convertToEntityAttribute(String s) {
        if (s==null){
            return null;
        }
        return Stream.of(LearningStage.values()).filter(learningStage -> learningStage.getCode().equals(s))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

    }
}
