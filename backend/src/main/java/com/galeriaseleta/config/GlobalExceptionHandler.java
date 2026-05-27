// ============================================================
// ARQUIVO: GlobalExceptionHandler.java
// FUNÇÃO: Captura erros que acontecem em qualquer parte do backend
// e converte em respostas HTTP padronizadas com código de status correto.
//
// Sem este arquivo, quando um erro ocorre, o Spring retornaria uma
// resposta genérica pouco útil. Com ele, controlamos exatamente o
// que o frontend recebe quando algo dá errado.
//
// FLUXO: Erro lançado no Service → capturado aqui → resposta JSON formatada
// ============================================================

package com.galeriaseleta.config;

// HttpStatus: enum com todos os códigos HTTP (200, 404, 409, 500, etc.)
import org.springframework.http.HttpStatus;
// ResponseEntity: permite montar a resposta HTTP com status + corpo
import org.springframework.http.ResponseEntity;
// @ExceptionHandler: marca o método que trata determinado tipo de exceção
import org.springframework.web.bind.annotation.ExceptionHandler;
// @RestControllerAdvice: diz ao Spring que esta classe intercepta erros
// de todos os controllers da aplicação
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime; // Para registrar o momento do erro
import java.util.Map;           // Para montar o corpo da resposta como mapa chave/valor

// @RestControllerAdvice: esta anotação faz com que o Spring aplique esta classe
// globalmente — qualquer exceção lançada em qualquer Controller passa por aqui.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // @ExceptionHandler(RuntimeException.class): este método é chamado automaticamente
    // sempre que qualquer parte do código lança uma RuntimeException.
    // RuntimeException é a classe base dos erros em tempo de execução no Java.
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {

        // Pega a mensagem do erro. Se for nula, usa "Erro interno" como padrão.
        String message = ex.getMessage() != null ? ex.getMessage() : "Erro interno";

        // Determina qual código HTTP usar baseado no texto da mensagem de erro
        HttpStatus status = resolverStatus(message);

        // Monta e retorna a resposta JSON com três campos:
        // - status: o código numérico HTTP (ex: 404)
        // - erro: a mensagem explicando o que aconteceu
        // - timestamp: o momento exato em que o erro ocorreu
        return ResponseEntity.status(status).body(Map.of(
                "status", status.value(),
                "erro", message,
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    // Analisa o texto da mensagem de erro e decide qual código HTTP retornar.
    // Esta é a lógica de mapeamento: texto do erro → código HTTP.
    private HttpStatus resolverStatus(String message) {
        String lower = message.toLowerCase(); // Converte para minúsculas para comparar sem distinção de caso

        // 404 Not Found: recurso não existe no banco de dados
        if (lower.contains("não encontrado") || lower.contains("nao encontrado")) {
            return HttpStatus.NOT_FOUND; // 404
        }

        // 409 Conflict: tentativa de criar algo que já existe
        if (lower.contains("já cadastrado") || lower.contains("ja cadastrado")
                || lower.contains("duplicado") || lower.contains("já existe")) {
            return HttpStatus.CONFLICT; // 409
        }

        // 401 Unauthorized: credenciais erradas no login
        if (lower.contains("senha incorreta") || lower.contains("credenciais inválidas")
                || lower.contains("credenciais invalidas")) {
            return HttpStatus.UNAUTHORIZED; // 401
        }

        // 501 Not Implemented: funcionalidade ainda não desenvolvida
        if (lower.contains("não implementado") || lower.contains("nao implementado")) {
            return HttpStatus.NOT_IMPLEMENTED; // 501
        }

        // 500 Internal Server Error: qualquer outro erro não mapeado acima
        return HttpStatus.INTERNAL_SERVER_ERROR; // 500
    }
}
