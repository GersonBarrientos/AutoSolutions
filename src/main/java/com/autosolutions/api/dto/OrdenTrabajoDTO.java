package com.autosolutions.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdenTrabajoDTO {

    /* NUEVO: requerido por las vistas (crear: null / editar: valor) */
    private Long id;

    @NotNull
    private Long vehiculoId;

    @NotNull
    private Integer estadoId;

    private Long empleadoId; // opcional

    /* Ajuste: patrón compatible con <input type="datetime-local"> */
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaIngreso;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaSalidaEstimada;

    private Long kmIngreso;
    private String diagnostico;
    private String observaciones;

    @Valid
    private List<LineaServicio> servicios = new ArrayList<>();

    @Valid
    private List<LineaRepuesto> repuestos = new ArrayList<>();

    /* ===== Sub-DTOs ===== */
    public static class LineaServicio {
        private Long servicioId;
        private String descripcion;
        @NotNull @DecimalMin("0.00") private BigDecimal cantidad;
        @NotNull @DecimalMin("0.00") private BigDecimal precioUnit;
        private BigDecimal totalLinea;
        public Long getServicioId() { return servicioId; }
        public void setServicioId(Long servicioId) { this.servicioId = servicioId; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        public BigDecimal getCantidad() { return cantidad; }
        public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }
        public BigDecimal getPrecioUnit() { return precioUnit; }
        public void setPrecioUnit(BigDecimal precioUnit) { this.precioUnit = precioUnit; }
        public BigDecimal getTotalLinea() { return totalLinea; }
        public void setTotalLinea(BigDecimal totalLinea) { this.totalLinea = totalLinea; }
    }

    public static class LineaRepuesto {
        @NotNull private Long repuestoId;
        private String descripcion;
        @NotNull @DecimalMin("0.00") private BigDecimal cantidad;
        @NotNull @DecimalMin("0.00") private BigDecimal precioUnit;
        private BigDecimal totalLinea;
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

    /* ===== Getters/Setters ===== */
    public Long getId() { return id; }                       // NUEVO
    public void setId(Long id) { this.id = id; }             // NUEVO
    public Long getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(Long vehiculoId) { this.vehiculoId = vehiculoId; }
    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }
    public Long getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }
    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDateTime fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public LocalDateTime getFechaSalidaEstimada() { return fechaSalidaEstimada; }
    public void setFechaSalidaEstimada(LocalDateTime fechaSalidaEstimada) { this.fechaSalidaEstimada = fechaSalidaEstimada; }
    public Long getKmIngreso() { return kmIngreso; }
    public void setKmIngreso(Long kmIngreso) { this.kmIngreso = kmIngreso; }
    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public List<LineaServicio> getServicios() { return servicios; }
    public void setServicios(List<LineaServicio> servicios) { this.servicios = servicios; }
    public List<LineaRepuesto> getRepuestos() { return repuestos; }
    public void setRepuestos(List<LineaRepuesto> repuestos) { this.repuestos = repuestos; }
}
