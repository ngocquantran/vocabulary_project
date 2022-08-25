package com.example.myvocab.model.enummodel;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class OrderStatusConverter implements AttributeConverter<OrderStatus, String> {
    @Override
    public String convertToDatabaseColumn(OrderStatus orderStatus) {
        if (orderStatus == null) {
            return null;
        }
        return orderStatus.getCode();
    }

    @Override
    public OrderStatus convertToEntityAttribute(final String code) {
        if (code == null) {
            return null;
        }
        return Stream.of(OrderStatus.values()).filter(orderStatus -> orderStatus.getCode().equals(code)).findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
