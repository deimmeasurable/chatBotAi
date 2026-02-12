package org.example.aichatbot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // 1. Handle the "Failed to parse AI response" errors
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleParsingError(RuntimeException ex) {
        log.error("AI Service Error: {}", ex.getMessage());

        Map<String, Object> error = new HashMap<>();
        error.put("status", 422); // Unprocessable Entity
        error.put("error", "AI_PARSING_FAILURE");
        error.put("message", ex.getMessage()); // Returns the "Failed to parse..." message
        error.put("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    // 2. Handle Barcode Not Found (The IllegalArgumentException we threw earlier)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(IllegalArgumentException ex) {
        log.warn("Validation/Lookup error: {}", ex.getMessage());

        Map<String, Object> error = new HashMap<>();
        error.put("status", 404);
        error.put("error", "NOT_FOUND");
        error.put("message", ex.getMessage());
        error.put("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 3. Handle JSON Syntax Errors in the Request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(HttpMessageNotReadableException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", 400);
        error.put("message", "The request body is malformed or invalid.");

        return ResponseEntity.badRequest().body(error);
    }

    // 4. The "Safety Net" for everything else
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
        log.error("CRITICAL ERROR: ", ex);

        Map<String, Object> error = new HashMap<>();
        error.put("status", 500);
        error.put("message", "A server-side error occurred.");
        error.put("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
