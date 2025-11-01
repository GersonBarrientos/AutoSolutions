package com.autosolutions.web;

import com.autosolutions.domain.Servicio;
import com.autosolutions.service.ServicioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.StringJoiner;

@Controller
@RequestMapping("/servicios")
public class ServicioPageController {

    private final ServicioService servicioService;

    public ServicioPageController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    // LISTA con búsqueda y paginación
    @GetMapping
    public String lista(@RequestParam(value = "q", required = false) String q,
                        @RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "size", defaultValue = "10") int size,
                        Model model) {
        var pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by("nombre").ascending());
        Page<Servicio> p = servicioService.listar(q, pageable);
        model.addAttribute("page", p);
        model.addAttribute("servicios", p.getContent());
        model.addAttribute("q", q);
        return "servicios/list";
    }

    // EXPORT CSV
    @GetMapping(value = "/export.csv", produces = "text/csv")
    @ResponseBody
    public String exportCsv(@RequestParam(value = "q", required = false) String q) {
        var rows = servicioService.listar(q);
        StringJoiner sj = new StringJoiner("\n");
        sj.add("id;nombre;precio;activo");
        rows.forEach(s -> sj.add(
            (s.getId() == null ? "" : s.getId()) + ";" +
            (s.getNombre() == null ? "" : s.getNombre().replace(";", ","))
            + ";" + (s.getPrecioUnit() == null ? "0.00" : s.getPrecioUnit())
            + ";" + (Boolean.TRUE.equals(s.getActivo()) ? "1" : "0")
        ));
        return sj.toString();
    }

    // DETALLE
    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return servicioService.buscarPorId(id)
                .map(servicio -> {
                    model.addAttribute("servicio", servicio);
                    return "servicios/detalle";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("mensajeError", "Servicio no encontrado");
                    return "redirect:/servicios";
                });
    }

    // NUEVO
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("servicio", new Servicio());
        return "servicios/form";
    }

    // EDITAR
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return servicioService.buscarPorId(id)
                .map(s -> {
                    model.addAttribute("servicio", s);
                    return "servicios/form";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("mensajeError", "Servicio no encontrado");
                    return "redirect:/servicios";
                });
    }

    // CREAR/ACTUALIZAR
    @PostMapping
    public String guardar(@Valid @ModelAttribute("servicio") Servicio servicio,
                          BindingResult br,
                          RedirectAttributes ra) {
        if (br.hasErrors()) {
            return "servicios/form";
        }
        try {
            servicioService.guardar(servicio);
            ra.addFlashAttribute("mensajeExito", "Servicio guardado correctamente");
            return "redirect:/servicios";
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("mensajeError", ex.getMessage());
            return (servicio.getId() == null)
                    ? "redirect:/servicios/nuevo"
                    : "redirect:/servicios/" + servicio.getId() + "/editar";
        }
    }

    // ELIMINAR
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            servicioService.eliminar(id);
            ra.addFlashAttribute("mensajeExito", "Servicio eliminado");
        } catch (Exception ex) {
            ra.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/servicios";
    }

    // TOGGLE ACTIVO
    @PostMapping("/{id}/toggle-activo")
    public String toggleActivo(@PathVariable Long id, RedirectAttributes ra) {
        try {
            servicioService.toggleActivo(id);
            ra.addFlashAttribute("mensajeExito", "Estado actualizado");
        } catch (Exception ex) {
            ra.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/servicios";
    }
}
