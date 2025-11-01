package com.autosolutions.web;

import com.autosolutions.domain.Repuesto;
import com.autosolutions.service.RepuestoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.StringJoiner;

@Controller
@RequestMapping("/repuestos")
public class RepuestoPageController {

    private final RepuestoService repuestoService;

    public RepuestoPageController(RepuestoService repuestoService) {
        this.repuestoService = repuestoService;
    }

    private Pageable buildPageable(int page, int size, String sort, String dir) {
        Sort s = (sort == null || sort.isBlank()) ? Sort.by("nombre") : Sort.by(sort);
        s = "desc".equalsIgnoreCase(dir) ? s.descending() : s.ascending();
        return PageRequest.of(Math.max(0, page), Math.max(1, size), s);
    }

    @GetMapping
    public String lista(@RequestParam(value = "q", required = false) String q,
                        @RequestParam(value = "page", defaultValue = "0") int page,
                        @RequestParam(value = "size", defaultValue = "10") int size,
                        @RequestParam(value = "sort", required = false) String sort,
                        @RequestParam(value = "dir", required = false) String dir,
                        Model model) {
        Pageable pageable = buildPageable(page, size, sort, dir);
        Page<Repuesto> p = repuestoService.listar(q, pageable);
        model.addAttribute("page", p);
        model.addAttribute("repuestos", p.getContent());
        model.addAttribute("q", q);
        model.addAttribute("sort", (sort == null || sort.isBlank()) ? "nombre" : sort);
        model.addAttribute("dir", (dir == null || dir.isBlank()) ? "asc" : dir);
        return "repuestos/list";
    }

    @GetMapping(value = "/export.csv", produces = "text/csv")
    @ResponseBody
    public String exportCsv(@RequestParam(value = "q", required = false) String q) {
        var rows = repuestoService.listar(q);
        StringJoiner sj = new StringJoiner("\n");
        sj.add("id;nombre;precio;stock;activo");
        rows.forEach(r -> sj.add(
            (r.getId() == null ? "" : r.getId()) + ";" +
            (r.getNombre() == null ? "" : r.getNombre().replace(";", ",")) + ";" +
            (r.getPrecioUnit() == null ? "0.00" : r.getPrecioUnit()) + ";" +
            (r.getStock() == null ? "0.00" : r.getStock()) + ";" +
            (Boolean.TRUE.equals(r.getActivo()) ? "1" : "0")
        ));
        return sj.toString();
    }

    @PostMapping("/import")
    public String importCsv(@RequestParam("file") MultipartFile file, RedirectAttributes ra) {
        try {
            int n = repuestoService.importarCsv(file);
            ra.addFlashAttribute("mensajeExito", "Importación completada: " + n + " registros");
        } catch (Exception e) {
            ra.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/repuestos";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return repuestoService.buscarPorId(id)
                .map(r -> {
                    model.addAttribute("repuesto", r);
                    return "repuestos/detalle";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("mensajeError", "Repuesto no encontrado");
                    return "redirect:/repuestos";
                });
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("repuesto", new Repuesto());
        return "repuestos/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return repuestoService.buscarPorId(id)
                .map(r -> {
                    model.addAttribute("repuesto", r);
                    return "repuestos/form";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("mensajeError", "Repuesto no encontrado");
                    return "redirect:/repuestos";
                });
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute("repuesto") Repuesto repuesto,
                          BindingResult br,
                          RedirectAttributes ra,
                          Model model) {
        if (br.hasErrors()) {
            return "repuestos/form";
        }
        try {
            repuestoService.guardar(repuesto);
            ra.addFlashAttribute("mensajeExito", "Repuesto guardado correctamente");
            return "redirect:/repuestos";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            ra.addFlashAttribute("mensajeError", ex.getMessage());
            return (repuesto.getId() == null)
                    ? "redirect:/repuestos/nuevo"
                    : "redirect:/repuestos/" + repuesto.getId() + "/editar";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            repuestoService.eliminar(id);
            ra.addFlashAttribute("mensajeExito", "Repuesto eliminado");
        } catch (Exception ex) {
            ra.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/repuestos";
    }

    @PostMapping("/{id}/toggle-activo")
    public String toggleActivo(@PathVariable Long id, RedirectAttributes ra) {
        try {
            repuestoService.toggleActivo(id);
            ra.addFlashAttribute("mensajeExito", "Estado actualizado");
        } catch (Exception ex) {
            ra.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/repuestos";
    }
}
