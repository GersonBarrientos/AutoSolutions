package com.autosolutions.web;

import com.autosolutions.api.dto.OrdenTrabajoDTO;
import com.autosolutions.domain.OrdenTrabajo;
import com.autosolutions.repo.EmpleadoRepository;
import com.autosolutions.repo.EstadoOrdenRepository;
import com.autosolutions.repo.RepuestoRepository;
import com.autosolutions.repo.ServicioRepository;
import com.autosolutions.repo.VehiculoRepository;
import com.autosolutions.service.OrdenTrabajoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;

@Slf4j
@Controller
@RequestMapping("/ordenes")
@RequiredArgsConstructor
public class OrdenTrabajoPageController {

  private final OrdenTrabajoService service;

  // Catálogos para el formulario
  private final VehiculoRepository vehiculoRepo;
  private final EstadoOrdenRepository estadoRepo;
  private final RepuestoRepository repuestoRepo;
  private final ServicioRepository servicioRepo;
  private final EmpleadoRepository empleadoRepo;

  /* ========================= LISTA ========================= */
  @GetMapping
  public String list(Model model) {
    model.addAttribute("ordenes", service.listar());
    return "ordenes/list";
  }

  /* ==================== NUEVA ORDEN (FORM) ==================== */
  @GetMapping("/nueva")
  public String nueva(Model model) {
    model.addAttribute("orden", new OrdenTrabajoDTO());
    cargarCatalogos(model);
    return "ordenes/form";
  }

  /* ====================== DETALLE (READ) ====================== */
  @GetMapping("/{id}")
  public String detalle(@PathVariable Long id, Model model, RedirectAttributes ra) {
    var opt = service.buscarPorId(id);
    if (opt.isEmpty()) {
      ra.addFlashAttribute("error", "Orden no encontrada");
      return "redirect:/ordenes";
    }
    OrdenTrabajo orden = opt.get();
    model.addAttribute("orden", orden); // La vista detalle espera la entidad
    return "ordenes/detalle";
  }

  /* =================== EDITAR (CARGAR FORM) =================== */
  @GetMapping("/{id}/editar")
  public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
    try {
      OrdenTrabajoDTO dto = service.obtenerDTO(id);
      model.addAttribute("orden", dto); // El form trabaja con el DTO
      model.addAttribute("ordenId", id);  // <--- CORRECCIÓN: Añadido para th:action y título
      cargarCatalogos(model);
      return "ordenes/form";
    } catch (NoSuchElementException ex) {
      ra.addFlashAttribute("error", "Orden no encontrada");
      return "redirect:/ordenes";
    }
  }

  /* ======================= CREAR (POST) ======================= */
  @PostMapping
  public String crear(@Valid @ModelAttribute("orden") OrdenTrabajoDTO dto,
                      BindingResult br,
                      Model model,
                      RedirectAttributes ra) {
    if (br.hasErrors()) {
      cargarCatalogos(model);
      return "ordenes/form";
    }
    OrdenTrabajo saved = service.crearOrden(dto);
    ra.addFlashAttribute("success", "Orden creada correctamente");
    return "redirect:/ordenes/" + saved.getId();
  }

  /* ===================== ACTUALIZAR (POST) ==================== */
  @PostMapping("/{id}")
  public String actualizar(@PathVariable Long id,
                           @Valid @ModelAttribute("orden") OrdenTrabajoDTO dto,
                           BindingResult br,
                           Model model,
                           RedirectAttributes ra) {
    if (br.hasErrors()) {
      model.addAttribute("ordenId", id); // <--- CORRECCIÓN: Reinyectado para th:action en error
      cargarCatalogos(model);
      return "ordenes/form";
    }
    try {
      service.actualizarOrden(id, dto);
      ra.addFlashAttribute("success", "Orden actualizada");
      return "redirect:/ordenes/" + id;
    } catch (NoSuchElementException ex) {
      ra.addFlashAttribute("error", "Orden no encontrada");
      return "redirect:/ordenes";
    }
  }

  /* ======================= ELIMINAR (POST) ===================== */
  @PostMapping("/{id}/eliminar")
  public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
    try {
      service.eliminarOrden(id);
      ra.addFlashAttribute("success", "Orden eliminada");
    } catch (NoSuchElementException ex) {
      ra.addFlashAttribute("error", "Orden no encontrada");
    }
    return "redirect:/ordenes";
  }

  /* ======================= CATÁLOGOS FORM ====================== */
  private void cargarCatalogos(Model model) {
    model.addAttribute("vehiculos", vehiculoRepo.findAll());
    model.addAttribute("estados", estadoRepo.findAll());
    model.addAttribute("repuestos", repuestoRepo.findAll());
    model.addAttribute("servicios", servicioRepo.findAll());
    model.addAttribute("empleados", empleadoRepo.findAll());
  }
}