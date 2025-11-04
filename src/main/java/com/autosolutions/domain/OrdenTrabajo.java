package com.autosolutions.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "ORDEN_TRABAJO", schema = "GERSON")
@SequenceGenerator(
        name = "ORDEN_TRABAJO_SEQ_GEN",
        sequenceName = "GERSON.ORDEN_TRABAJO_SEQ",
        allocationSize = 1
)
public class OrdenTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORDEN_TRABAJO_SEQ_GEN")
    @Column(name = "ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "VEHICULO_ID", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ESTADO_ID", nullable = false)
    private EstadoOrden estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPLEADO_ID")
    private Empleado empleado; // Asignado a la orden

    @Column(name = "FECHA_INGRESO", nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(name = "FECHA_SALIDA_ESTIMADA")
    private LocalDateTime fechaSalidaEstimada;

    @Column(name = "KM_INGRESO")
    private Long kmIngreso;

    @Column(name = "DIAGNOSTICO", length = 2000)
    private String diagnostico;

    @Column(name = "OBSERVACIONES", length = 2000)
    private String observaciones;

    @Column(name = "SUBTOTAL_MANO_OBRA", precision = 12, scale = 2, nullable = false)
    private BigDecimal subtotalManoObra = BigDecimal.ZERO;

    @Column(name = "SUBTOTAL_REPUESTOS", precision = 12, scale = 2, nullable = false)
    private BigDecimal subtotalRepuestos = BigDecimal.ZERO;

    @Column(name = "DESCUENTO", precision = 12, scale = 2, nullable = false)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(name = "IMPUESTO", precision = 12, scale = 2, nullable = false)
    private BigDecimal impuesto = BigDecimal.ZERO;

    @Column(name = "TOTAL", precision = 12, scale = 2, nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    @Version
    @Column(name = "VERSION")
    private Long version;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DetalleOrden> detalles = new LinkedHashSet<>();

    /* ====== Callbacks ====== */
    @PrePersist
    public void prePersist() {
        var now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.fechaIngreso == null) this.fechaIngreso = now;
        if (subtotalManoObra == null) subtotalManoObra = BigDecimal.ZERO;
        if (subtotalRepuestos == null) subtotalRepuestos = BigDecimal.ZERO;
        if (descuento == null) descuento = BigDecimal.ZERO;
        if (impuesto == null) impuesto = BigDecimal.ZERO;
        if (total == null) total = BigDecimal.ZERO;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /* ====== Helpers ====== */
    public void addDetalle(DetalleOrden d) {
        d.setOrden(this);
        this.detalles.add(d);
    }

    public void clearDetalles() {
        for (DetalleOrden d : this.detalles) {
            d.setOrden(null);
        }
        this.detalles.clear();
    }

    /* ====== Getters/Setters ====== */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }

    public EstadoOrden getEstado() { return estado; }
    public void setEstado(EstadoOrden estado) { this.estado = estado; }

    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

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

    public BigDecimal getSubtotalManoObra() { return subtotalManoObra; }
    public void setSubtotalManoObra(BigDecimal subtotalManoObra) { this.subtotalManoObra = subtotalManoObra; }

    public BigDecimal getSubtotalRepuestos() { return subtotalRepuestos; }
    public void setSubtotalRepuestos(BigDecimal subtotalRepuestos) { this.subtotalRepuestos = subtotalRepuestos; }

    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }

    public BigDecimal getImpuesto() { return impuesto; }
    public void setImpuesto(BigDecimal impuesto) { this.impuesto = impuesto; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Set<DetalleOrden> getDetalles() { return detalles; }
    public void setDetalles(Set<DetalleOrden> detalles) { this.detalles = detalles; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrdenTrabajo that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return 31; }
}
