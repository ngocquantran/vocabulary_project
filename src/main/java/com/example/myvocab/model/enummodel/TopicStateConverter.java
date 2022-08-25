package com.example.myvocab.model.enummodel;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class TopicStateConverter implements AttributeConverter<TopicState, String> {
    @Override
    public String convertToDatabaseColumn(TopicState topicState) {
        if (topicState == null) {
            return null;
        }
        return topicState.getCode();
    }

    @Override
    public TopicState convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(TopicState.values()).filter(u -> u.getCode().equals(code)).findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
