// ============================================================
// ARQUIVO: LocalDateTimeConverter.java
// FUNÇÃO: Conversor automático entre LocalDateTime (Java) e String (SQLite).
//
// O PROBLEMA:
// O SQLite não tem um tipo nativo de data/hora como o MySQL tem.
// Ele armazena datas como texto (String). O Java usa LocalDateTime.
// Sem este conversor, o Hibernate não saberia como salvar/ler datas.
//
// A SOLUÇÃO:
// Este conversor intercepta automaticamente toda operação de data:
// - Ao salvar: converte LocalDateTime → String (formato "yyyy-MM-dd HH:mm:ss")
// - Ao ler: converte String → LocalDateTime
//
// @Converter(autoApply = true): aplica este conversor automaticamente
// para TODOS os campos LocalDateTime de TODAS as entidades.
// Você não precisa anotar cada campo — ele funciona globalmente.
// ============================================================

package com.galeriaseleta.converter;

// AttributeConverter: interface do JPA para conversão entre tipo Java e tipo do banco
import jakarta.persistence.AttributeConverter;
// @Converter(autoApply = true): ativa este conversor globalmente para LocalDateTime
import jakarta.persistence.Converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;  // Para formatar/parsear datas como String
import java.time.format.DateTimeParseException; // Exceção lançada se a string não for uma data válida

// AttributeConverter<LocalDateTime, String>:
// - LocalDateTime: tipo no Java (no objeto)
// - String: tipo no banco de dados (na coluna)
@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {

    // Formato padrão para armazenar datas no banco: "2024-05-26 14:30:00"
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Chamado automaticamente ao SALVAR no banco
    // Converte LocalDateTime → String para armazenar no SQLite
    @Override
    public String convertToDatabaseColumn(LocalDateTime attribute) {
        return attribute != null ? attribute.format(FORMATTER) : null;
    }

    // Chamado automaticamente ao LER do banco
    // Converte String → LocalDateTime para usar no Java
    @Override
    public LocalDateTime convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return null;
        try {
            // Tenta o formato padrão: "yyyy-MM-dd HH:mm:ss"
            return LocalDateTime.parse(dbData, FORMATTER);
        } catch (DateTimeParseException e) {
            // Fallback: tenta o formato ISO 8601 caso existam dados salvos em outro formato
            // Ex: "2024-05-26T14:30:00" (com 'T' no meio)
            return LocalDateTime.parse(dbData, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }
}
