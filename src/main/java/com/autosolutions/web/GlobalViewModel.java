package com.autosolutions.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Component
@ControllerAdvice
public class GlobalViewModel {

  @Value("${spring.application.name:AutoSolutions}")
  private String appName;

  @ModelAttribute
  public void addCommonAttributes(Model model, Authentication auth, HttpServletRequest req) {
    // disponibles en TODAS las vistas
    model.addAttribute("appName", appName);
    model.addAttribute("profile", auth != null ? auth.getName() : "Invitado");

    // usado por el navbar para marcar activo
    String path = req.getRequestURI();            // ej: /ordenes/123/editar
    model.addAttribute("currentPath", path != null ? path : "/");
  }
}
