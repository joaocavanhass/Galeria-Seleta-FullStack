package com.galeriaseleta.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Erro interno";
        HttpStatus status = resolverStatus(message);
        return ResponseEntity.status(status).body(Map.of(
                "status", status.value(),
                "erro", message,
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    private HttpStatus resolverStatus(String message) {
        String lower = message.toLowerCase();
        if (lower.contains("não encontrado") || lower.contains("nao encontrado")) {
            return HttpStatus.NOT_FOUND;
        }
        if (lower.contains("já cadastrado") || lower.contains("ja cadastrado")
                || lower.contains("duplicado") || lower.contains("já existe")) {
            return HttpStatus.CONFLICT;
        }
        if (lower.contains("senha incorreta") || lower.contains("credenciais inválidas")
                || lower.contains("credenciais invalidas")) {
            return HttpStatus.UNAUTHORIZED;
        }
        if (lower.contains("não implementado") || lower.contains("nao implementado")) {
            return HttpStatus.NOT_IMPLEMENTED;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
