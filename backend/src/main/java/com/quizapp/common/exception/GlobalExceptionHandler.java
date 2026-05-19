package com.quizapp.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Uygulama genelinde istisna yönetimi.
 *
 * @RestControllerAdvice: Tüm @RestController sınıflarına uygulanır.
 * Fırlatılan istisnalar burada yakalanır, tutarlı JSON yanıt formatına dönüştürülür.
 *
 * Hata yanıt formatı:
 * {
 *   "timestamp": "...",
 *   "status":    404,
 *   "message":   "Quiz bulunamadı"
 * }
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Uygulama iş mantığı hataları (kayıt bulunamadı, çakışma vb.) */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
        return buildResponse(ex.getMessage(), ex.getStatus());
    }

    /**
     * @Valid ile işaretlenmiş request body'lerindeki doğrulama hataları.
     * Alan adı → hata mesajı şeklinde döner.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError err : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(err.getField(), err.getDefaultMessage());
        }
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("message", "Doğrulama hatası");
        body.put("errors", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    /** Spring Security: kimlik doğrulama başarısız (401) */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuth(AuthenticationException ex) {
        return buildResponse("Kimlik doğrulama başarısız: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    /** Spring Security: yetkisiz erişim (403) */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccess(AccessDeniedException ex) {
        return buildResponse("Bu işlem için yetkiniz yok", HttpStatus.FORBIDDEN);
    }

    /** Beklenmedik hatalar (500) */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return buildResponse("Sunucu hatası: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(String message, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
