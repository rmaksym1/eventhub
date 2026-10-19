package com.eventservice.exception;

import com.eventhub.common.exception.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(EntityNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND,
                ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT,
                "Request conflicts with existing data, check for duplicates or related entities!");
    }

    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}
