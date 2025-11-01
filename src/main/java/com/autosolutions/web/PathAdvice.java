package com.autosolutions.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Expone la URI actual como 'path' para usar en las plantillas.
 * Evita el uso de #request / #httpServletRequest que en Thymeleaf 3.1
 * ya no están disponibles por defecto.
 */
@ControllerAdvice
public class PathAdvice {

  @ModelAttribute("path")
  public String path(HttpServletRequest req) {
    return req != null ? req.getRequestURI() : "";
  }
}
