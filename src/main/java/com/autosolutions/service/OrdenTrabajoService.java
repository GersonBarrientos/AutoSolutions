package com.autosolutions.service;

import com.autosolutions.api.dto.OrdenTrabajoDTO;
import com.autosolutions.domain.*;
import com.autosolutions.repo.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class OrdenTrabajoService {

    private static final int SCALE = 2;
    private static final RoundingMode RM = RoundingMode.HALF_UP;
    private static final int MAX_TXT = 1000; // tope seguro para descripcion/diagnostico/observaciones

    private final OrdenTrabajoRepository ordenRepo;
    private final VehiculoRepository vehiculoRepo;
    private final EstadoOrdenRepository estadoRepo;
    private final EmpleadoRepository empleadoRepo;
    private final ServicioRepository servicioRepo;
    private final RepuestoRepository repuestoRepo;

    public OrdenTrabajoService(OrdenTrabajoRepository ordenRepo,
                               VehiculoRepository vehiculoRepo,
                               EstadoOrdenRepository estadoRepo,
                               EmpleadoRepository empleadoRepo,
                               ServicioRepository servicioRepo,
                               RepuestoRepository repuestoRepo) {
        this.ordenRepo = ordenRepo;
        this.vehiculoRepo = vehiculoRepo;
        this.estadoRepo = estadoRepo;
        this.empleadoRepo = empleadoRepo;
        this.servicioRepo = servicioRepo;
        this.repuestoRepo = repuestoRepo;
    }

    /* ========================= Lectura ========================= */

    public List<OrdenTrabajo> listar() {
        try {
            return ordenRepo.findAllWithVehiculoEstadoOrderByIdDesc();
        } catch (Exception ex) {
            return ordenRepo.findAll();
        }
    }

    public Optional<OrdenTrabajo> buscarPorId(Long id) {
        return ordenRepo.findWithDetallesById(id).or(() -> ordenRepo.findById(id));
    }

    /* ==================== Crear / Actualizar ==================== */

    @Transactional
    public OrdenTrabajo crearOrden(OrdenTrabajoDTO dto) {
        OrdenTrabajo orden = new OrdenTrabajo();
        mapCabecera(orden, dto);
        fillAndCalculate(orden, dto);
        return ordenRepo.save(orden);
    }

    @Transactional
    public OrdenTrabajo actualizarOrden(Long id, OrdenTrabajoDTO dto) {
        OrdenTrabajo orden = ordenRepo.findWithDetallesById(id)
                .orElseThrow(() -> new NoSuchElementException("Orden no encontrada: " + id));
        mapCabecera(orden, dto);
        orden.clearDetalles();
        fillAndCalculate(orden, dto);
        return ordenRepo.save(orden);
    }

    @Transactional
    public void eliminarOrden(Long id) {
        if (!ordenRepo.existsById(id)) {
            throw new NoSuchElementException("Orden no encontrada: " + id);
        }
        ordenRepo.deleteById(id);
    }

    /* ========================= Helpers ========================= */

    private void mapCabecera(OrdenTrabajo orden, OrdenTrabajoDTO dto) {
        Vehiculo vehiculo = vehiculoRepo.findById(dto.getVehiculoId())
                .orElseThrow(() -> new NoSuchElementException("Vehículo no existe: " + dto.getVehiculoId()));
        EstadoOrden estado = estadoRepo.findById(dto.getEstadoId())
                .orElseThrow(() -> new NoSuchElementException("Estado no existe: " + dto.getEstadoId()));

        orden.setVehiculo(vehiculo);
        orden.setEstado(estado);

        if (dto.getEmpleadoId() != null) {
            Empleado emp = empleadoRepo.findById(dto.getEmpleadoId())
                    .orElseThrow(() -> new NoSuchElementException("Empleado no existe: " + dto.getEmpleadoId()));
            orden.setEmpleado(emp);
        } else {
            orden.setEmpleado(null);
        }

        // Fechas
        orden.setFechaIngreso(dto.getFechaIngreso() != null ? dto.getFechaIngreso() : LocalDateTime.now());
        orden.setFechaSalidaEstimada(dto.getFechaSalidaEstimada());

        // Otros campos
        orden.setKmIngreso(dto.getKmIngreso());
        orden.setDiagnostico(safeTrim(dto.getDiagnostico(), MAX_TXT));
        orden.setObservaciones(safeTrim(dto.getObservaciones(), MAX_TXT));
    }

    private static BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private static BigDecimal norm2(BigDecimal v) {
        return nvl(v).setScale(SCALE, RM);
    }

    private static String safeTrim(String s, int maxLen) {
        if (s == null) return null;
        String t = s.trim();
        if (t.length() > maxLen) return t.substring(0, maxLen);
        return t;
    }

    private void fillAndCalculate(OrdenTrabajo orden, OrdenTrabajoDTO dto) {
        BigDecimal subServicios = BigDecimal.ZERO;
        BigDecimal subRepuestos = BigDecimal.ZERO;

        /* ------------------- Servicios (mano de obra) ------------------- */
        if (dto.getServicios() != null) {
            for (var ls : dto.getServicios()) {
                if (ls == null) continue;

                BigDecimal cantidad = nvl(ls.getCantidad());
                BigDecimal precio   = nvl(ls.getPrecioUnit());

                // Reglas mínimas: ignorar líneas sin cantidad o con precio negativo
                if (cantidad.compareTo(BigDecimal.ZERO) <= 0) continue;
                if (precio.compareTo(BigDecimal.ZERO) < 0) continue;

                DetalleOrden det = new DetalleOrden();
                det.setTipo(TipoDetalle.SERVICIO);

                if (ls.getServicioId() != null) {
                    Servicio s = servicioRepo.findById(ls.getServicioId())
                            .orElseThrow(() -> new NoSuchElementException("Servicio no existe: " + ls.getServicioId()));
                    det.setServicio(s);
                    // Descripción: catálogo si no vino una
                    String desc = (ls.getDescripcion() == null || ls.getDescripcion().isBlank())
                            ? s.getNombre()
                            : ls.getDescripcion();
                    det.setDescripcion(safeTrim(desc, MAX_TXT));
                } else {
                    // Línea libre (sin vínculo a catálogo)
                    det.setDescripcion(safeTrim(ls.getDescripcion(), MAX_TXT));
                }

                det.setCantidad(norm2(cantidad));
                det.setPrecioUnit(norm2(precio));

                BigDecimal totalLinea = det.getCantidad().multiply(det.getPrecioUnit()).setScale(SCALE, RM);
                det.setTotalLinea(totalLinea);

                orden.addDetalle(det);
                subServicios = subServicios.add(totalLinea);
            }
        }

        /* ------------------------ Repuestos ------------------------ */
        if (dto.getRepuestos() != null) {
            for (var lr : dto.getRepuestos()) {
                if (lr == null || lr.getRepuestoId() == null) continue;

                BigDecimal cantidad = nvl(lr.getCantidad());
                BigDecimal precio   = nvl(lr.getPrecioUnit());

                if (cantidad.compareTo(BigDecimal.ZERO) <= 0) continue;
                if (precio.compareTo(BigDecimal.ZERO) < 0) continue;

                Repuesto r = repuestoRepo.findById(lr.getRepuestoId())
                        .orElseThrow(() -> new NoSuchElementException("Repuesto no existe: " + lr.getRepuestoId()));

                DetalleOrden det = new DetalleOrden();
                det.setTipo(TipoDetalle.REPUESTO);
                det.setRepuesto(r);

                String desc = (lr.getDescripcion() == null || lr.getDescripcion().isBlank())
                        ? r.getNombre()
                        : lr.getDescripcion();
                det.setDescripcion(safeTrim(desc, MAX_TXT));

                det.setCantidad(norm2(cantidad));
                det.setPrecioUnit(norm2(precio));

                BigDecimal totalLinea = det.getCantidad().multiply(det.getPrecioUnit()).setScale(SCALE, RM);
                det.setTotalLinea(totalLinea);

                orden.addDetalle(det);
                subRepuestos = subRepuestos.add(totalLinea);
            }
        }

        // Totales en cabecera
        orden.setSubtotalManoObra(subServicios.setScale(SCALE, RM));
        orden.setSubtotalRepuestos(subRepuestos.setScale(SCALE, RM));

        // descuento / impuesto si los manejas en otro flujo; aquí quedan en su valor actual
        BigDecimal total = subServicios.add(subRepuestos);
        orden.setTotal(total.setScale(SCALE, RM));
    }

    /* ======================= DTO Mapping ======================= */

    public OrdenTrabajoDTO obtenerDTO(Long id) {
        OrdenTrabajo ot = buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Orden no encontrada: " + id));

        OrdenTrabajoDTO dto = new OrdenTrabajoDTO();
        dto.setVehiculoId(ot.getVehiculo().getId());
        dto.setEstadoId(ot.getEstado().getId()); // EstadoOrden usa Integer en tu repo
        dto.setEmpleadoId(ot.getEmpleado() != null ? ot.getEmpleado().getId() : null);
        dto.setFechaIngreso(ot.getFechaIngreso());
        dto.setFechaSalidaEstimada(ot.getFechaSalidaEstimada());
        dto.setKmIngreso(ot.getKmIngreso());
        dto.setDiagnostico(ot.getDiagnostico());
        dto.setObservaciones(ot.getObservaciones());

        for (DetalleOrden d : ot.getDetalles()) {
            if (d.getTipo() == TipoDetalle.SERVICIO) {
                OrdenTrabajoDTO.LineaServicio ls = new OrdenTrabajoDTO.LineaServicio();
                ls.setServicioId(d.getServicio() != null ? d.getServicio().getId() : null);
                ls.setDescripcion(d.getDescripcion());
                ls.setCantidad(d.getCantidad());
                ls.setPrecioUnit(d.getPrecioUnit());
                ls.setTotalLinea(d.getTotalLinea());
                dto.getServicios().add(ls);
            } else if (d.getTipo() == TipoDetalle.REPUESTO) {
                OrdenTrabajoDTO.LineaRepuesto lr = new OrdenTrabajoDTO.LineaRepuesto();
                lr.setRepuestoId(d.getRepuesto() != null ? d.getRepuesto().getId() : null);
                lr.setDescripcion(d.getDescripcion());
                lr.setCantidad(d.getCantidad());
                lr.setPrecioUnit(d.getPrecioUnit());
                lr.setTotalLinea(d.getTotalLinea());
                dto.getRepuestos().add(lr);
            }
        }
        return dto;
    }
}
