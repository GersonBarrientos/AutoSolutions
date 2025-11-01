package com.autosolutions.api;

import com.autosolutions.domain.Repuesto;
import com.autosolutions.service.RepuestoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/repuestos")
public class RepuestoController {

    private final RepuestoService repuestoService;

    public RepuestoController(RepuestoService repuestoService) {
        this.repuestoService = repuestoService;
    }

    @GetMapping
    public List<Repuesto> listar(@RequestParam(value = "q", required = false) String q) {
        return repuestoService.listar(q);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Repuesto> obtener(@PathVariable Long id) {
        return repuestoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Repuesto> crear(@RequestBody Repuesto r) {
        Repuesto guardado = repuestoService.guardar(r);
        return ResponseEntity.created(URI.create("/api/repuestos/" + guardado.getId())).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Repuesto> actualizar(@PathVariable Long id, @RequestBody Repuesto r) {
        r.setId(id);
        return ResponseEntity.ok(repuestoService.guardar(r));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        repuestoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/incrementar")
    public ResponseEntity<Repuesto> incrementar(@PathVariable Long id, @RequestParam("cantidad") BigDecimal cantidad) {
        return ResponseEntity.ok(repuestoService.incrementarStock(id, cantidad));
    }

    @PostMapping("/{id}/decrementar")
    public ResponseEntity<Repuesto> decrementar(@PathVariable Long id, @RequestParam("cantidad") BigDecimal cantidad) {
        return ResponseEntity.ok(repuestoService.decrementarStock(id, cantidad));
    }
}
