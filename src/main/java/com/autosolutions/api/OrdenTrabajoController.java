package com.autosolutions.api;

import com.autosolutions.api.dto.OrdenTrabajoDTO;
import com.autosolutions.domain.OrdenTrabajo;
import com.autosolutions.service.OrdenTrabajoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
public class OrdenTrabajoController {

    private final OrdenTrabajoService service;

    // GET /api/ordenes
    @GetMapping
    public ResponseEntity<List<OrdenTrabajo>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    // GET /api/ordenes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<OrdenTrabajo> obtener(@PathVariable Long id) {
        Optional<OrdenTrabajo> ordenOpt = service.buscarPorId(id);
        return ordenOpt
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/ordenes/{id}/dto  -> para cargar el form de edición
    @GetMapping("/{id}/dto")
    public ResponseEntity<OrdenTrabajoDTO> obtenerDTO(@PathVariable Long id) {
        OrdenTrabajoDTO dto = service.obtenerDTO(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    // POST /api/ordenes
    @PostMapping
    public ResponseEntity<Void> crear(@RequestBody OrdenTrabajoDTO dto) {
        Long id = service.crearOrden(dto);
        return ResponseEntity.created(URI.create("/api/ordenes/" + id)).build();
    }

    // PUT /api/ordenes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(@PathVariable Long id,
                                           @RequestBody OrdenTrabajoDTO dto) {
        service.actualizarOrden(id, dto);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/ordenes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminarOrden(id);
        return ResponseEntity.noContent().build();
    }
}
