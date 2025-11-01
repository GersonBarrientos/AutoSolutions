package com.autosolutions.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "com.autosolutions.api")
public class ApiExceptionHandler {

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(IllegalArgumentException.class)
  public Map<String, Object> handleBadRequest(IllegalArgumentException ex) {
    log.warn("400 Bad Request: {}", ex.getMessage());
    return Map.of("error", ex.getMessage());
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public Map<String, Object> handleServerError(Exception ex) {
    log.error("500 Error inesperado", ex);
    return Map.of("error", "Error inesperado");
  }
}
