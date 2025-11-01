package com.autosolutions.web;

import com.autosolutions.domain.Cliente;
import com.autosolutions.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClientePageController {

  private static final String ATTR_CLIENTE = "cliente";
  private static final String REDIR_LIST   = "redirect:/clientes";

  private final ClienteService clienteService;

  // LISTADO con paginación y búsqueda
  @GetMapping
  public String lista(@RequestParam(value = "q", required = false) String q,
                      @RequestParam(value = "page", defaultValue = "0") int page,
                      @RequestParam(value = "size", defaultValue = "10") int size,
                      Model model) {
      
      var pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by("nombres").ascending());
      Page<Cliente> p = clienteService.buscar(q, pageable);
      
      model.addAttribute("page", p);
      model.addAttribute("clientes", p.getContent()); 
      model.addAttribute("q", q);
      
      return "clientes/list";
  }

  // FORM NUEVO
  @GetMapping("/nuevo")
  public String nuevo(Model model) {
    model.addAttribute(ATTR_CLIENTE, new Cliente());
    return "clientes/form";
  }

  // FORM EDITAR
  @GetMapping("/{id}/editar")
  public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
    return clienteService.findById(id)
        .map(c -> {
            model.addAttribute(ATTR_CLIENTE, c);
            return "clientes/form";
        })
        .orElseGet(() -> {
            ra.addFlashAttribute("mensajeError", "Cliente no encontrado (ID: " + id + ")");
            return REDIR_LIST;
        });
  }

  // DETALLE
  @GetMapping("/{id}")
  public String detalle(@PathVariable Long id, Model model, RedirectAttributes ra) {
    return clienteService.findById(id)
        .map(c -> {
            model.addAttribute(ATTR_CLIENTE, c);
            return "clientes/detalle";
        })
        .orElseGet(() -> {
            ra.addFlashAttribute("mensajeError", "Cliente no encontrado (ID: " + id + ")");
            return REDIR_LIST;
        });
  }

  // CREAR / ACTUALIZAR (Método 'guardar' unificado)
  @PostMapping
  public String guardar(@Valid @ModelAttribute(ATTR_CLIENTE) Cliente cliente,
                        BindingResult br,
                        RedirectAttributes ra) {
    
    if (br.hasErrors()) {
        return "clientes/form";
    }

    try {
        clienteService.guardar(cliente);
        ra.addFlashAttribute("mensajeExito", "Cliente guardado correctamente");
        return REDIR_LIST;
    } catch (IllegalArgumentException ex) {
        // Errores de negocio (duplicados)
        // Agrega el error al BindingResult para mostrarlo en el formulario
        br.reject("error.cliente", ex.getMessage());
        return "clientes/form";
    } catch (Exception e) {
        // Otros errores (BD, etc.)
        br.reject("error.cliente", "Error inesperado al guardar: " + e.getMessage());
        return "clientes/form";
    }
  }

  // ELIMINAR
  @PostMapping("/{id}/eliminar")
  public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
    try {
        clienteService.eliminar(id);
        ra.addFlashAttribute("mensajeExito", "Cliente eliminado correctamente");
    } catch (Exception ex) {
        ra.addFlashAttribute("mensajeError", "Error al eliminar: " + ex.getMessage());
    }
    return REDIR_LIST;
  }
}