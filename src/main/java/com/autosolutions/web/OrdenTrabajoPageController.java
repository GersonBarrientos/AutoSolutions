package com.autosolutions.web;

import com.autosolutions.api.dto.OrdenTrabajoDTO;
import com.autosolutions.domain.OrdenTrabajo;
import com.autosolutions.repo.EstadoOrdenRepository;
import com.autosolutions.repo.OrdenTrabajoRepository;
import com.autosolutions.repo.RepuestoRepository;
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

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/ordenes")
@RequiredArgsConstructor
public class OrdenTrabajoPageController {

  private final OrdenTrabajoService service;
  private final OrdenTrabajoRepository ordenRepo;
  private final VehiculoRepository vehiculoRepo;
  private final EstadoOrdenRepository estadoRepo;
  private final RepuestoRepository repuestoRepo;

  @GetMapping
  public String list(Model model) {
    try {
      model.addAttribute("ordenes", ordenRepo.findAllWithVehiculoEstadoOrderByIdDesc());
    } catch (Exception e) {
      model.addAttribute("ordenes", ordenRepo.findAllByOrderByIdDesc());
    }
    return "ordenes/list";
  }

  @GetMapping("/nueva")
  public String nueva(Model model) {
    model.addAttribute("orden", new OrdenTrabajoDTO());
    cargarCatalogos(model);
    return "ordenes/form";
  }

  @GetMapping("/{id}")
  public String detalle(@PathVariable Long id, Model model, RedirectAttributes ra) {
    OrdenTrabajo orden = null;
    try {
      orden = ordenRepo.findWithDetallesById(id);
    } catch (Exception ignored) { }

    if (orden == null) {
      Optional<OrdenTrabajo> fallback = ordenRepo.findById(id);
      if (fallback.isEmpty()) {
        ra.addFlashAttribute("error", "Orden no encontrada");
        return "redirect:/ordenes";
      }
      orden = fallback.get();
    }

    model.addAttribute("orden", orden);
    return "ordenes/detalle";
  }

  @GetMapping("/{id}/editar")
  public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
    OrdenTrabajoDTO dto = service.obtenerDTO(id);
    if (dto == null) {
      ra.addFlashAttribute("error", "Orden no encontrada");
      return "redirect:/ordenes";
    }
    model.addAttribute("orden", dto);
    cargarCatalogos(model);
    return "ordenes/form";
  }

  @PostMapping
  public String crear(@Valid @ModelAttribute("orden") OrdenTrabajoDTO dto,
                      BindingResult br, Model model, RedirectAttributes ra) {
    if (br.hasErrors()) {
      cargarCatalogos(model);
      return "ordenes/form";
    }
    Long id = service.crearOrden(dto);
    ra.addFlashAttribute("success", "Orden creada correctamente");
    return "redirect:/ordenes/" + id;
  }

  @PostMapping("/{id}")
  public String actualizar(@PathVariable Long id,
                           @Valid @ModelAttribute("orden") OrdenTrabajoDTO dto,
                           BindingResult br, Model model, RedirectAttributes ra) {
    if (br.hasErrors()) {
      cargarCatalogos(model);
      return "ordenes/form";
    }
    service.actualizarOrden(id, dto);
    ra.addFlashAttribute("success", "Orden actualizada");
    return "redirect:/ordenes/" + id;
  }

  @PostMapping("/{id}/eliminar")
  public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
    service.eliminarOrden(id);
    ra.addFlashAttribute("success", "Orden eliminada");
    return "redirect:/ordenes";
  }

  private void cargarCatalogos(Model model) {
    model.addAttribute("vehiculos", vehiculoRepo.findAll());
    model.addAttribute("estados", estadoRepo.findAll());
    model.addAttribute("repuestos", repuestoRepo.findAll());
  }
}
