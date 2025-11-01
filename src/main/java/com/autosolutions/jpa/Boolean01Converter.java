package com.autosolutions.jpa;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Convierte Boolean <-> NUMBER(1,0) (1 = true, 0/null = false).
 */
@Converter(autoApply = false)
public class Boolean01Converter implements AttributeConverter<Boolean, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Boolean value) {
        return Boolean.TRUE.equals(value) ? 1 : 0;
    }

    @Override
    public Boolean convertToEntityAttribute(Integer dbValue) {
        return dbValue != null && dbValue.intValue() == 1;
    }
}
