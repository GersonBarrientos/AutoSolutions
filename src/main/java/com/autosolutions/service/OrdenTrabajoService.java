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
import java.time.LocalDateTime;
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
        try {
            return ordenRepo.findAllWithVehiculoEstadoOrderByIdDesc();
        } catch (Exception e) {
            return ordenRepo.findAllByOrderByIdDesc();
        }
    }

    @Transactional(readOnly = true)
    public Optional<OrdenTrabajo> buscarPorId(Long id) {
        return ordenRepo.findById(id);
    }

    @Transactional(readOnly = true)
    public OrdenTrabajoDTO obtenerDTO(Long id) {
        OrdenTrabajo orden;
        try {
            orden = ordenRepo.findWithDetallesById(id);
        } catch (Exception e) {
            orden = ordenRepo.findById(id).orElse(null);
        }
        if (orden == null) return null;
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
        OrdenTrabajo orden;
        try {
            orden = ordenRepo.findWithDetallesById(id);
        } catch (Exception e) {
            orden = ordenRepo.findById(id).orElse(null);
        }
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
        // con cascade = ALL + orphanRemoval = true en OrdenTrabajo.detalles
        try {
            ordenRepo.deleteById(id);
        } catch (Exception ex) {
            ordenRepo.findById(id).ifPresent(ordenRepo::delete);
        }
    }

    /* ========== MAPEO DTO -> ENTITY y cálculo de totales ========== */

    private void fillAndCalculate(OrdenTrabajo orden, OrdenTrabajoDTO dto) {

        // --- relaciones principales ---
        Vehiculo vehiculo = vehiculoRepo.findById(dto.getVehiculoId())
                .orElseThrow(() -> new EntityNotFoundException("Vehículo no existe id=" + dto.getVehiculoId()));

        Long estadoIdLong = dto.getEstadoId();
        if (estadoIdLong == null) {
            throw new EntityNotFoundException("Estado de orden no existe id=null");
        }
        Integer estadoId = estadoIdLong.intValue();
        EstadoOrden estado = estadoRepo.findById(estadoId)
                .orElseThrow(() -> new EntityNotFoundException("Estado de orden no existe id=" + estadoIdLong));

        orden.setVehiculo(vehiculo);
        orden.setEstado(estado);

        // --- fechas y timestamps ---
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaIngreso = (dto.getFechaIngreso() != null) ? dto.getFechaIngreso() : ahora;
        orden.setFechaIngreso(fechaIngreso);
        orden.setFechaSalidaEstimada(dto.getFechaSalidaEstimada());

        if (orden.getId() == null) orden.setCreatedAt(ahora);
        orden.setUpdatedAt(ahora);

        // --- otros campos ---
        orden.setDiagnostico(dto.getDiagnostico());
        orden.setObservaciones(dto.getObservaciones());

        // --- limpiar detalles anteriores ---
        orden.getDetalles().clear();

        BigDecimal subtotalMO  = BigDecimal.ZERO;
        BigDecimal subtotalRep = BigDecimal.ZERO;

        // --- servicios (tipo SERVICIO) ---
        if (dto.getServicios() != null) {
            for (OrdenTrabajoDTO.ServicioLineaDTO l : dto.getServicios()) {
                if (l == null) continue;
                if (isLineaVaciaServicio(l)) continue;

                BigDecimal cantidad = positiveOrZero(l.getCantidad());
                BigDecimal precio   = nonNullScaled(l.getPrecioUnit());
                BigDecimal totalLinea = cantidad.multiply(precio).setScale(2, RoundingMode.HALF_UP);

                DetalleOrden det = DetalleOrden.builder()
                        .orden(orden)
                        .tipo(TipoDetalle.SERVICIO)
                        .repuesto(null)
                        .descripcion(l.getDescripcion())
                        .cantidad(cantidad)
                        .precioUnit(precio)
                        .totalLinea(totalLinea)
                        .build();

                orden.getDetalles().add(det);
                subtotalMO = subtotalMO.add(totalLinea);
            }
        }

        // --- repuestos (tipo REPUESTO) ---
        if (dto.getRepuestos() != null) {
            for (OrdenTrabajoDTO.RepuestoLineaDTO l : dto.getRepuestos()) {
                if (l == null) continue;
                if (isLineaVaciaRepuesto(l)) continue;

                Repuesto rep = repuestoRepo.findById(l.getRepuestoId())
                        .orElseThrow(() -> new EntityNotFoundException("Repuesto no existe id=" + l.getRepuestoId()));

                BigDecimal cantidad = positiveOrZero(l.getCantidad());
                // si no viene precio en el DTO, se usa el precio del repuesto en BD
                BigDecimal precio = (l.getPrecioUnit() == null)
                        ? nonNullScaled(rep.getPrecioUnit())
                        : nonNullScaled(l.getPrecioUnit());
                BigDecimal totalLinea = cantidad.multiply(precio).setScale(2, RoundingMode.HALF_UP);

                DetalleOrden det = DetalleOrden.builder()
                        .orden(orden)
                        .tipo(TipoDetalle.REPUESTO)
                        .repuesto(rep)
                        .descripcion((l.getDescripcion() != null && !l.getDescripcion().isBlank())
                                ? l.getDescripcion()
                                : rep.getNombre())
                        .cantidad(cantidad)
                        .precioUnit(precio)
                        .totalLinea(totalLinea)
                        .build();

                orden.getDetalles().add(det);
                subtotalRep = subtotalRep.add(totalLinea);
            }
        }

        // --- totales ---
        orden.setSubtotalManoObra(subtotalMO.setScale(2, RoundingMode.HALF_UP));
        orden.setSubtotalRepuestos(subtotalRep.setScale(2, RoundingMode.HALF_UP));
        BigDecimal totalGeneral = subtotalMO.add(subtotalRep).setScale(2, RoundingMode.HALF_UP);
        orden.setTotal(totalGeneral);

        // si tu DTO tiene 'total', lo actualizamos (opcional)
        dto.setTotal(totalGeneral);
    }

    private boolean isLineaVaciaServicio(OrdenTrabajoDTO.ServicioLineaDTO l) {
        if (l.getDescripcion() == null || l.getDescripcion().isBlank()) return true;
        if (l.getCantidad() == null) return true;
        if (l.getPrecioUnit() == null) return true;
        return false;
    }

    /** Para repuestos exigimos repuestoId y cantidad. El precio es opcional. */
    private boolean isLineaVaciaRepuesto(OrdenTrabajoDTO.RepuestoLineaDTO l) {
        if (l.getRepuestoId() == null) return true;
        if (l.getCantidad() == null) return true;
        return false;
    }

    private BigDecimal nonNullScaled(BigDecimal v) {
        return (v == null ? BigDecimal.ZERO : v).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal positiveOrZero(BigDecimal v) {
        BigDecimal x = nonNullScaled(v);
        return x.signum() < 0 ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : x;
    }

    /* ========== MAPEO ENTITY -> DTO (para editar/ver) ========== */

    private OrdenTrabajoDTO toDTO(OrdenTrabajo orden) {
        OrdenTrabajoDTO dto = new OrdenTrabajoDTO();

        dto.setId(orden.getId());
        dto.setVehiculoId(orden.getVehiculo() != null ? orden.getVehiculo().getId() : null);
        dto.setEstadoId(orden.getEstado() != null && orden.getEstado().getId() != null ? orden.getEstado().getId().longValue() : null);
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
                s.setTotalLinea(d.getTotalLinea());
                servicios.add(s);
            } else if (d.getTipo() == TipoDetalle.REPUESTO) {
                OrdenTrabajoDTO.RepuestoLineaDTO r = new OrdenTrabajoDTO.RepuestoLineaDTO();
                if (d.getRepuesto() != null) {
                    r.setRepuestoId(d.getRepuesto().getId());
                    r.setDescripcion(d.getRepuesto().getNombre());
                } else {
                    r.setDescripcion(d.getDescripcion());
                }
                r.setCantidad(d.getCantidad());
                r.setPrecioUnit(d.getPrecioUnit());
                r.setTotalLinea(d.getTotalLinea());
                repuestos.add(r);
            }
        }

        dto.setServicios(servicios);
        dto.setRepuestos(repuestos);

        return dto;
    }
}
