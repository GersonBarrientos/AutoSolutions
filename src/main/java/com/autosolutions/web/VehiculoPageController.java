package com.autosolutions.web;

import com.autosolutions.domain.Vehiculo;
import com.autosolutions.domain.Cliente;
import com.autosolutions.domain.Marca;
import com.autosolutions.domain.Modelo;
import com.autosolutions.repo.ClienteRepository;
import com.autosolutions.repo.MarcaRepository;
import com.autosolutions.repo.ModeloRepository;
import com.autosolutions.service.VehiculoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/vehiculos")
public class VehiculoPageController {

  private final VehiculoService vehiculoService;
  private final ClienteRepository clienteRepo;
  private final MarcaRepository marcaRepo;
  private final ModeloRepository modeloRepo;

  public VehiculoPageController(VehiculoService vehiculoService,
                                ClienteRepository clienteRepo,
                                MarcaRepository marcaRepo,
                                ModeloRepository modeloRepo) {
    this.vehiculoService = vehiculoService;
    this.clienteRepo = clienteRepo;
    this.marcaRepo = marcaRepo;
    this.modeloRepo = modeloRepo;
  }

  // LISTA
  @GetMapping
  public String listar(@RequestParam(required = false) String q,
                       @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                       Model model) {
    var page = vehiculoService.listarPaginado(q, pageable);
    model.addAttribute("page", page);
    model.addAttribute("q", q);
    return "vehiculos/list";
  }

  // NUEVO
  @GetMapping("/nuevo")
  public String nuevo(Model model) {
    prepararForm(model, new Vehiculo());
    return "vehiculos/form";
  }

  // EDITAR
  @GetMapping("/{id}/editar")
  public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
    var vOpt = vehiculoService.buscarPorId(id);
    if (vOpt.isEmpty()) {
      flash.addFlashAttribute("mensajeError", "Vehículo no encontrado.");
      return "redirect:/vehiculos";
    }
    prepararForm(model, vOpt.get());
    return "vehiculos/form";
  }

  // DETALLE
  @GetMapping("/{id}")
  public String detalle(@PathVariable Long id, Model model, RedirectAttributes flash) {
    var vOpt = vehiculoService.buscarPorId(id);
    if (vOpt.isEmpty()) {
      flash.addFlashAttribute("mensajeError", "Vehículo no encontrado.");
      return "redirect:/vehiculos";
    }
    model.addAttribute("vehiculo", vOpt.get());
    return "vehiculos/detalle";
  }

  // GUARDAR (create/update)
  @PostMapping
  public String guardar(@Valid @ModelAttribute("vehiculo") Vehiculo vehiculo,
                        BindingResult br,
                        @RequestParam(required = false) Long clienteId,
                        @RequestParam(required = false) Long marcaId,
                        @RequestParam(required = false) Long modeloId,
                        Model model,
                        RedirectAttributes flash) {

    if (clienteId == null) {
      br.reject("global", "Debe seleccionar un cliente.");
    }
    if (br.hasErrors()) {
      prepararListas(model, marcaId);
      return "vehiculos/form";
    }

    try {
      vehiculoService.guardar(vehiculo, clienteId, marcaId, modeloId);
      flash.addFlashAttribute("mensajeExito", "Vehículo guardado correctamente.");
      return "redirect:/vehiculos";
    } catch (IllegalArgumentException ex) {
      br.reject("global", ex.getMessage());
      prepararListas(model, marcaId);
      return "vehiculos/form";
    } catch (Exception ex) {
      br.reject("global", "Ocurrió un error al guardar.");
      prepararListas(model, marcaId);
      return "vehiculos/form";
    }
  }

  // ELIMINAR
  @PostMapping("/{id}/eliminar")
  public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
    try {
      vehiculoService.eliminar(id);
      flash.addFlashAttribute("mensajeExito", "Vehículo eliminado correctamente.");
    } catch (NoSuchElementException ex) {
      flash.addFlashAttribute("mensajeError", ex.getMessage());
    } catch (IllegalStateException ex) {
      flash.addFlashAttribute("mensajeError", ex.getMessage());
    } catch (Exception ex) {
      flash.addFlashAttribute("mensajeError", "Ocurrió un error al eliminar.");
    }
    return "redirect:/vehiculos";
  }

  // Helpers
  private void prepararForm(Model model, Vehiculo v) {
    model.addAttribute("vehiculo", v);
    prepararListas(model, v.getMarca() != null ? v.getMarca().getId() : null);
  }

  private void prepararListas(Model model, Long marcaId) {
    List<Cliente> clientes = clienteRepo.findAll(Sort.by("nombres").ascending());
    List<Marca> marcas = marcaRepo.findAll(Sort.by("nombre").ascending());
    List<Modelo> modelos = (marcaId != null) ? modeloRepo.findByMarcaIdOrderByNombreAsc(marcaId) : List.of();

    model.addAttribute("clientes", clientes);
    model.addAttribute("marcas", marcas);
    model.addAttribute("modelos", modelos);
  }
}

