package com.autosolutions.api;

import com.autosolutions.domain.Vehiculo;
import com.autosolutions.service.VehiculoService; // Usar el servicio
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("/api/vehiculos")
@RequiredArgsConstructor
public class VehiculoController {

    private final VehiculoService vehiculoService; // Inyectar el servicio

    // DTO Actualizado con IDs para relaciones
    public record VehiculoRequest(
            @NotNull(message = "clienteId es obligatorio") Long clienteId,
            @NotBlank(message = "placa es obligatoria") String placa,
            Long marcaId,   // Opcional
            Long modeloId,  // Opcional
            Integer anio,
            String color,
            String vin
    ) {}

    // Wrapper para errores
    public record ApiError(String error) {}

    // --- Consultas ---

    @GetMapping
    public Page<Vehiculo> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "placa,asc") String sort,
            @RequestParam(required = false) String q) {

        Sort ord = Sort.by(sort.split(","));
        Pageable pageable = PageRequest.of(page, Math.max(1, size), ord);
        return vehiculoService.listarPaginado(q, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehiculo> obtener(@PathVariable Long id) {
        return vehiculoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-cliente/{clienteId}")
    public List<Vehiculo> porCliente(@PathVariable Long clienteId) {
        return vehiculoService.listarPorCliente(clienteId);
    }

    // --- Comandos ---

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody VehiculoRequest req) {
        try {
            // ✅ SIN Lombok builder: usar constructor vacío + setters
            Vehiculo nuevoVehiculo = new Vehiculo();
            nuevoVehiculo.setPlaca(req.placa());
            nuevoVehiculo.setAnio(req.anio());
            nuevoVehiculo.setColor(req.color());
            nuevoVehiculo.setVin(req.vin());

            Vehiculo creado = vehiculoService.guardar(
                    nuevoVehiculo, req.clienteId(), req.marcaId(), req.modeloId());

            return ResponseEntity
                    .created(URI.create("/api/vehiculos/" + creado.getId()))
                    .body(creado);

        } catch (IllegalArgumentException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(new ApiError(e.getMessage()));
        } catch (Exception e) {
            log.error("Error al crear vehículo via API", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Error interno al crear vehículo"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody VehiculoRequest req) {
        try {
            Vehiculo vehiculoExistente = vehiculoService.buscarPorId(id)
                    .orElseThrow(() -> new NoSuchElementException("Vehículo no encontrado con ID: " + id));

            vehiculoExistente.setPlaca(req.placa());
            vehiculoExistente.setAnio(req.anio());
            vehiculoExistente.setColor(req.color());
            vehiculoExistente.setVin(req.vin());

            Vehiculo guardado = vehiculoService.guardar(
                    vehiculoExistente, req.clienteId(), req.marcaId(), req.modeloId());

            return ResponseEntity.ok(guardado);

        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiError(e.getMessage()));
        } catch (Exception e) {
            log.error("Error al actualizar vehículo {} via API", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError("Error interno al actualizar vehículo"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            vehiculoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) { // Error por FK
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            log.error("Error al eliminar vehículo {} via API", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
