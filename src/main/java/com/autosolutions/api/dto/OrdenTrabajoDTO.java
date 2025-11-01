package com.autosolutions.api.dto;

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

    private Long vehiculoId;
    private Integer estadoId;

    private LocalDateTime fechaIngreso;
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

    public Integer getEstadoId() { return estadoId; }
    public void setEstadoId(Integer estadoId) { this.estadoId = estadoId; }

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

    /**
     * Línea de tipo SERVICIO.
     * Ej: "Cambio de aceite", cantidad 1, precio 15.00
     */
    public static class ServicioLineaDTO {
        private String descripcion;
        private BigDecimal cantidad = BigDecimal.ONE;
        private BigDecimal precioUnit = BigDecimal.ZERO;

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

        public BigDecimal getCantidad() { return cantidad; }
        public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }

        public BigDecimal getPrecioUnit() { return precioUnit; }
        public void setPrecioUnit(BigDecimal precioUnit) { this.precioUnit = precioUnit; }
    }

    /**
     * Línea de tipo REPUESTO.
     * Aquí sí hay vínculo a un repuesto existente en base de datos.
     */
    public static class RepuestoLineaDTO {
        private Long repuestoId;
        private String descripcion; // opcional mostrar nombre comercial en el form
        private BigDecimal cantidad = BigDecimal.ONE;
        private BigDecimal precioUnit = BigDecimal.ZERO;

        public Long getRepuestoId() { return repuestoId; }
        public void setRepuestoId(Long repuestoId) { this.repuestoId = repuestoId; }

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

        public BigDecimal getCantidad() { return cantidad; }
        public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }

        public BigDecimal getPrecioUnit() { return precioUnit; }
        public void setPrecioUnit(BigDecimal precioUnit) { this.precioUnit = precioUnit; }
    }
}
