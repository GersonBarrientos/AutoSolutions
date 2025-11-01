package com.autosolutions.service;

import com.autosolutions.api.dto.OrdenTrabajoDTO;
import com.autosolutions.domain.*;
import com.autosolutions.repo.EstadoOrdenRepository;
import com.autosolutions.repo.OrdenTrabajoRepository;
import com.autosolutions.repo.RepuestoRepository;
import com.autosolutions.repo.VehiculoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrdenTrabajoService {

    private final OrdenTrabajoRepository ordenRepo;
    private final VehiculoRepository vehiculoRepo;
    private final EstadoOrdenRepository estadoRepo;
    private final RepuestoRepository repuestoRepo;

    /* ========== LISTAR / BUSCAR ========== */

    @Transactional(readOnly = true)
    public List<OrdenTrabajo> listar() {
        // preferimos el método con fetch para vistas
        try {
            return ordenRepo.findAllWithVehiculoEstadoOrderByIdDesc();
        } catch (Exception e) {
            // fallback por si falla el fetch join
            return ordenRepo.findAllByOrderByIdDesc();
        }
    }

    @Transactional(readOnly = true)
    public Optional<OrdenTrabajo> buscarPorId(Long id) {
        return ordenRepo.findById(id);
    }

    @Transactional(readOnly = true)
    public OrdenTrabajoDTO obtenerDTO(Long id) {
        OrdenTrabajo orden = ordenRepo.findWithDetallesById(id);
        if (orden == null) {
            return null;
        }
        return toDTO(orden);
    }

    /* ========== CREAR / ACTUALIZAR / BORRAR ========== */

    public Long crearOrden(OrdenTrabajoDTO dto) {
        OrdenTrabajo orden = new OrdenTrabajo();
        fillAndCalculate(orden, dto);
        orden = ordenRepo.save(orden);
        return orden.getId();
    }

    public void actualizarOrden(Long id, OrdenTrabajoDTO dto) {
        OrdenTrabajo orden = ordenRepo.findWithDetallesById(id);
        if (orden == null) {
            throw new EntityNotFoundException("OrdenTrabajo no encontrada con id " + id);
        }
        fillAndCalculate(orden, dto);
        ordenRepo.save(orden);
    }

    public void eliminarOrden(Long id) {
        if (!ordenRepo.existsById(id)) {
            throw new EntityNotFoundException("OrdenTrabajo no encontrada con id " + id);
        }
        ordenRepo.deleteById(id);
    }

    /* ========== MAPEO DTO -> ENTITY y cálculo de totales ========== */

    /**
     * Copia datos básicos y reconstruye todas las líneas de detalle.
     * También recalcula el total general.
     */
    private void fillAndCalculate(OrdenTrabajo orden, OrdenTrabajoDTO dto) {

        // --- relaciones principales ---
        Vehiculo vehiculo = vehiculoRepo.findById(dto.getVehiculoId())
                .orElseThrow(() -> new EntityNotFoundException("Vehiculo no existe id=" + dto.getVehiculoId()));

        EstadoOrden estado = estadoRepo.findById(dto.getEstadoId())
                .orElseThrow(() -> new EntityNotFoundException("EstadoOrden no existe id=" + dto.getEstadoId()));

        orden.setVehiculo(vehiculo);
        orden.setEstado(estado);

        orden.setFechaIngreso(dto.getFechaIngreso());
        orden.setFechaSalidaEstimada(dto.getFechaSalidaEstimada());
        orden.setDiagnostico(dto.getDiagnostico());
        orden.setObservaciones(dto.getObservaciones());

        // --- limpiar detalles anteriores ---
        orden.getDetalles().clear();

        BigDecimal totalGeneral = BigDecimal.ZERO;

        // --- servicios (tipo SERVICIO) ---
        if (dto.getServicios() != null) {
            for (OrdenTrabajoDTO.ServicioLineaDTO linea : dto.getServicios()) {
                if (linea == null) continue;
                if (isLineaVaciaServicio(linea)) continue;

                BigDecimal cantidad = safe(linea.getCantidad());
                BigDecimal precio   = safe(linea.getPrecioUnit());
                BigDecimal totalLinea = cantidad.multiply(precio).setScale(2, RoundingMode.HALF_UP);

                DetalleOrden det = DetalleOrden.builder()
                        .orden(orden)
                        .tipo(TipoDetalle.SERVICIO)
                        .repuesto(null) // no aplica
                        .descripcion(linea.getDescripcion())
                        .cantidad(cantidad)
                        .precioUnit(precio)
                        .totalLinea(totalLinea)
                        .build();

                orden.getDetalles().add(det);
                totalGeneral = totalGeneral.add(totalLinea);
            }
        }

        // --- repuestos (tipo REPUESTO) ---
        if (dto.getRepuestos() != null) {
            for (OrdenTrabajoDTO.RepuestoLineaDTO linea : dto.getRepuestos()) {
                if (linea == null) continue;
                if (isLineaVaciaRepuesto(linea)) continue;

                Repuesto rep = repuestoRepo.findById(linea.getRepuestoId())
                        .orElseThrow(() -> new EntityNotFoundException("Repuesto no existe id=" + linea.getRepuestoId()));

                BigDecimal cantidad = safe(linea.getCantidad());
                BigDecimal precio   = safe(linea.getPrecioUnit());
                BigDecimal totalLinea = cantidad.multiply(precio).setScale(2, RoundingMode.HALF_UP);

                DetalleOrden det = DetalleOrden.builder()
                        .orden(orden)
                        .tipo(TipoDetalle.REPUESTO)
                        .repuesto(rep)
                        .descripcion(linea.getDescripcion() != null && !linea.getDescripcion().isBlank()
                                ? linea.getDescripcion()
                                : rep.getNombre()) // asumiendo Repuesto tiene getNombre()
                        .cantidad(cantidad)
                        .precioUnit(precio)
                        .totalLinea(totalLinea)
                        .build();

                orden.getDetalles().add(det);
                totalGeneral = totalGeneral.add(totalLinea);
            }
        }

        orden.setTotal(totalGeneral.setScale(2, RoundingMode.HALF_UP));
        dto.setTotal(orden.getTotal()); // sincronizar de regreso por si usas el dto luego
    }

    private boolean isLineaVaciaServicio(OrdenTrabajoDTO.ServicioLineaDTO l) {
        if (l.getDescripcion() == null || l.getDescripcion().isBlank()) return true;
        if (l.getCantidad() == null) return true;
        if (l.getPrecioUnit() == null) return true;
        return false;
    }

    private boolean isLineaVaciaRepuesto(OrdenTrabajoDTO.RepuestoLineaDTO l) {
        if (l.getRepuestoId() == null) return true;
        if (l.getCantidad() == null) return true;
        if (l.getPrecioUnit() == null) return true;
        return false;
    }

    private BigDecimal safe(BigDecimal v) {
        return (v == null ? BigDecimal.ZERO : v).setScale(2, RoundingMode.HALF_UP);
    }

    /* ========== MAPEO ENTITY -> DTO (para editar/ver) ========== */

    private OrdenTrabajoDTO toDTO(OrdenTrabajo orden) {
        OrdenTrabajoDTO dto = new OrdenTrabajoDTO();

        dto.setId(orden.getId());
        dto.setVehiculoId(orden.getVehiculo().getId()); // asumiendo Vehiculo tiene getId()
        dto.setEstadoId(orden.getEstado().getId());
        dto.setFechaIngreso(orden.getFechaIngreso());
        dto.setFechaSalidaEstimada(orden.getFechaSalidaEstimada());
        dto.setDiagnostico(orden.getDiagnostico());
        dto.setObservaciones(orden.getObservaciones());
        dto.setTotal(orden.getTotal());

        List<OrdenTrabajoDTO.ServicioLineaDTO> servicios = new ArrayList<>();
        List<OrdenTrabajoDTO.RepuestoLineaDTO> repuestos = new ArrayList<>();

        for (DetalleOrden d : orden.getDetalles()) {
            if (d.getTipo() == TipoDetalle.SERVICIO) {
                OrdenTrabajoDTO.ServicioLineaDTO s = new OrdenTrabajoDTO.ServicioLineaDTO();
                s.setDescripcion(d.getDescripcion());
                s.setCantidad(d.getCantidad());
                s.setPrecioUnit(d.getPrecioUnit());
                servicios.add(s);
            } else if (d.getTipo() == TipoDetalle.REPUESTO) {
                OrdenTrabajoDTO.RepuestoLineaDTO r = new OrdenTrabajoDTO.RepuestoLineaDTO();
                if (d.getRepuesto() != null) {
                    r.setRepuestoId(d.getRepuesto().getId()); // asumiendo Repuesto tiene getId()
                    r.setDescripcion(d.getRepuesto().getNombre()); // o d.getDescripcion()
                } else {
                    r.setDescripcion(d.getDescripcion());
                }
                r.setCantidad(d.getCantidad());
                r.setPrecioUnit(d.getPrecioUnit());
                repuestos.add(r);
            }
        }

        dto.setServicios(servicios);
        dto.setRepuestos(repuestos);

        return dto;
    }
}
