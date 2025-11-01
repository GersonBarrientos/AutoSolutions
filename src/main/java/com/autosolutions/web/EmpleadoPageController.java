package com.autosolutions.web;

import com.autosolutions.domain.Empleado;
import com.autosolutions.service.EmpleadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/empleados")
public class EmpleadoPageController {

  private final EmpleadoService service;

  private static final String VIEW_LIST   = "empleados/list";
  private static final String VIEW_FORM   = "empleados/form";
  private static final String VIEW_DETAIL = "empleados/detalle"; // <<-- ¡aquí el cambio!

  // LIST
  @GetMapping
  public String listar(@RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       Model model) {
    Page<Empleado> data = service.listar(q, PageRequest.of(page, 10, Sort.by("apellido","nombre")));
    model.addAttribute("q", q);
    model.addAttribute("page", data);
    return VIEW_LIST;
  }

  // DETALLE
  @GetMapping("/{id}")
  public String ver(@PathVariable Long id, Model model, RedirectAttributes ra) {
    return service.buscar(id).map(emp -> {
      model.addAttribute("empleado", emp);
      return VIEW_DETAIL; // <<-- ahora apunta a detalle.html
    }).orElseGet(() -> {
      ra.addFlashAttribute("error", "Empleado no encontrado");
      return "redirect:/empleados";
    });
  }

  // NUEVO
  @GetMapping("/nuevo")
  public String nuevo(Model model) {
    model.addAttribute("empleado", new Empleado());
    model.addAttribute("titulo", "Nuevo empleado");
    return VIEW_FORM;
  }

  // EDITAR
  @GetMapping("/{id}/editar")
  public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
    return service.buscar(id).map(emp -> {
      model.addAttribute("empleado", emp);
      model.addAttribute("titulo", "Editar empleado");
      return VIEW_FORM;
    }).orElseGet(() -> {
      ra.addFlashAttribute("error", "Empleado no encontrado");
      return "redirect:/empleados";
    });
  }

  // CREAR
  @PostMapping
  public String crear(@Valid @ModelAttribute("empleado") Empleado empleado,
                      BindingResult br, RedirectAttributes ra, Model model) {
    if (br.hasErrors()) {
      model.addAttribute("titulo", "Nuevo empleado");
      return VIEW_FORM;
    }
    try {
      service.crear(empleado);
      ra.addFlashAttribute("ok", "Empleado creado");
      return "redirect:/empleados";
    } catch (DataIntegrityViolationException ex) {
      model.addAttribute("titulo", "Nuevo empleado");
      model.addAttribute("error", "Datos duplicados (email o teléfono).");
      return VIEW_FORM;
    }
  }

  // ACTUALIZAR
  @PostMapping("/{id}")
  public String actualizar(@PathVariable Long id,
                           @Valid @ModelAttribute("empleado") Empleado empleado,
                           BindingResult br, RedirectAttributes ra, Model model) {
    if (br.hasErrors()) {
      model.addAttribute("titulo", "Editar empleado");
      return VIEW_FORM;
    }
    try {
      service.actualizar(id, empleado);
      ra.addFlashAttribute("ok", "Empleado actualizado");
      return "redirect:/empleados/" + id; // opcional: volver al detalle
    } catch (DataIntegrityViolationException ex) {
      model.addAttribute("titulo", "Editar empleado");
      model.addAttribute("error", "Datos duplicados (email o teléfono).");
      return VIEW_FORM;
    }
  }

  // ELIMINAR
  @PostMapping("/{id}/eliminar")
  public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
    service.eliminar(id);
    ra.addFlashAttribute("ok", "Empleado eliminado");
    return "redirect:/empleados";
  }
}
