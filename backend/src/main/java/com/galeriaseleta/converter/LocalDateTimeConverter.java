package com.galeriaseleta.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String convertToDatabaseColumn(LocalDateTime attribute) {
        return attribute != null ? attribute.format(FORMATTER) : null;
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;
        try {
            return LocalDateTime.parse(dbData, FORMATTER);
        } catch (DateTimeParseException e) {
            // Tenta formato ISO caso já exista algum dado salvo diferente
            return LocalDateTime.parse(dbData, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }
}
