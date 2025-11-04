package com.autosolutions.api;

import com.autosolutions.api.dto.OrdenTrabajoDTO;
import com.autosolutions.domain.OrdenTrabajo;
import com.autosolutions.service.OrdenTrabajoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenTrabajoController {

    private final OrdenTrabajoService service;

    public OrdenTrabajoController(OrdenTrabajoService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenTrabajo> getById(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/dto")
    public ResponseEntity<OrdenTrabajoDTO> getDto(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerDTO(id));
    }

    @PostMapping
    public ResponseEntity<OrdenTrabajo> create(@Valid @RequestBody OrdenTrabajoDTO dto) {
        OrdenTrabajo saved = service.crearOrden(dto);
        return ResponseEntity.created(URI.create("/api/ordenes/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdenTrabajo> update(@PathVariable Long id,
                                               @Valid @RequestBody OrdenTrabajoDTO dto) {
        return ResponseEntity.ok(service.actualizarOrden(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.eliminarOrden(id);
        return ResponseEntity.noContent().build();
    }
}
