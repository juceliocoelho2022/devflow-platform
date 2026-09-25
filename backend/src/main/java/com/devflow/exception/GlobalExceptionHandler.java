package com.devflow.exception;

import com.devflow.dto.ApiDtos.ApiError;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(BusinessException.class)
  ResponseEntity<ApiError> business(BusinessException ex) {
    return ResponseEntity.status(ex.getStatus()).body(
      new ApiError(ex.getStatus().value(), ex.getCode(), ex.getMessage(), LocalDateTime.now(), Map.of())
    );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex) {
    Map<String, String> fields = new LinkedHashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));
    return ResponseEntity.badRequest().body(
      new ApiError(400, "VALIDATION_ERROR", "Revise os campos informados.", LocalDateTime.now(), fields)
    );
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ApiError> unexpected(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
      new ApiError(500, "INTERNAL_ERROR", "Ocorreu um erro inesperado.", LocalDateTime.now(), Map.of())
    );
  }
}
