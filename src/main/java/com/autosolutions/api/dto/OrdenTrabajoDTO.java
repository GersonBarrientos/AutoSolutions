package com.autosolutions.api.dto;

import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para crear/editar/mostrar una Orden de Trabajo.
 * Separa líneas de SERVICIOS y REPUESTOS para que el formulario sea más fácil.
 */
public class OrdenTrabajoDTO {

    private Long id; // importante para modo edición en Thymeleaf

    @NotNull(message = "Vehículo es requerido")
    private Long vehiculoId;

    @NotNull(message = "Estado es requerido")
    private Long estadoId; // <<-- Long (consistente con repos/entidad)

    @NotNull(message = "Fecha de ingreso es requerida")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaIngreso;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaSalidaEstimada;

    private String diagnostico;
    private String observaciones;

    private BigDecimal total = BigDecimal.ZERO;

    // líneas capturadas en el form
    private List<ServicioLineaDTO> servicios = new ArrayList<>();
    private List<RepuestoLineaDTO> repuestos = new ArrayList<>();

    // ===== getters & setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(Long vehiculoId) { this.vehiculoId = vehiculoId; }

    public Long getEstadoId() { return estadoId; }
    public void setEstadoId(Long estadoId) { this.estadoId = estadoId; }

    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDateTime fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public LocalDateTime getFechaSalidaEstimada() { return fechaSalidaEstimada; }
    public void setFechaSalidaEstimada(LocalDateTime fechaSalidaEstimada) { this.fechaSalidaEstimada = fechaSalidaEstimada; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public List<ServicioLineaDTO> getServicios() { return servicios; }
    public void setServicios(List<ServicioLineaDTO> servicios) { this.servicios = servicios; }

    public List<RepuestoLineaDTO> getRepuestos() { return repuestos; }
    public void setRepuestos(List<RepuestoLineaDTO> repuestos) { this.repuestos = repuestos; }

    // ================== SUB-DTOS ==================

    /** Línea de tipo SERVICIO. */
    public static class ServicioLineaDTO {
        private String descripcion;
        private BigDecimal cantidad = BigDecimal.ONE;
        private BigDecimal precioUnit = BigDecimal.ZERO;
        private BigDecimal totalLinea = BigDecimal.ZERO; // <<-- usado por el form/servicio

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

        public BigDecimal getCantidad() { return cantidad; }
        public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }

        public BigDecimal getPrecioUnit() { return precioUnit; }
        public void setPrecioUnit(BigDecimal precioUnit) { this.precioUnit = precioUnit; }

        public BigDecimal getTotalLinea() { return totalLinea; }
        public void setTotalLinea(BigDecimal totalLinea) { this.totalLinea = totalLinea; }
    }

    /** Línea de tipo REPUESTO. */
    public static class RepuestoLineaDTO {
        private Long repuestoId;
        private String descripcion; // opcional
        private BigDecimal cantidad = BigDecimal.ONE;
        private BigDecimal precioUnit = BigDecimal.ZERO; // puede venir null en la práctica
        private BigDecimal totalLinea = BigDecimal.ZERO; // <<-- usado por el form/servicio

        public Long getRepuestoId() { return repuestoId; }
        public void setRepuestoId(Long repuestoId) { this.repuestoId = repuestoId; }

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

        public BigDecimal getCantidad() { return cantidad; }
        public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }

        public BigDecimal getPrecioUnit() { return precioUnit; }
        public void setPrecioUnit(BigDecimal precioUnit) { this.precioUnit = precioUnit; }

        public BigDecimal getTotalLinea() { return totalLinea; }
        public void setTotalLinea(BigDecimal totalLinea) { this.totalLinea = totalLinea; }
    }
}
