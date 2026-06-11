package com.obs.observaAcao.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Padroniza as respostas de erro como JSON { timestamp, status, error, message }
 * para que o front-end sempre receba um corpo legível e o status HTTP correto.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> body(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message
        ));
    }

    /** Regras de negócio / validação → 400 Bad Request */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /** Credenciais inválidas no login → 401 */
    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<Map<String, Object>> handleAuth(Exception ex) {
        return body(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.");
    }

    /** Sem permissão → 403 */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleDenied(AccessDeniedException ex) {
        return body(HttpStatus.FORBIDDEN, "Você não tem permissão para esta ação.");
    }

    /**
     * RuntimeException genérica usada pelas regras de negócio do projeto
     * (ex.: "Protocolo não encontrado", "categoria não permite anônimo").
     * Tratada como 400 para o front exibir a mensagem ao usuário.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "Erro ao processar a requisição.";
        if (msg.toLowerCase().contains("não encontrad")) {
            return body(HttpStatus.NOT_FOUND, msg);
        }
        return body(HttpStatus.BAD_REQUEST, msg);
    }
}
