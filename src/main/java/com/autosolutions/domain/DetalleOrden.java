package com.autosolutions.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "DETALLE_ORDEN", schema = "GERSON")
@SequenceGenerator(
        name = "DETALLE_ORDEN_SEQ_GEN",
        sequenceName = "GERSON.DETALLE_ORDEN_SEQ",
        allocationSize = 1
)
public class DetalleOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DETALLE_ORDEN_SEQ_GEN")
    @Column(name = "ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORDEN_ID", nullable = false)
    @JsonBackReference
    private OrdenTrabajo orden;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", length = 20, nullable = false)
    private TipoDetalle tipo; // SERVICIO | REPUESTO

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERVICIO_ID")
    private Servicio servicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPUESTO_ID")
    private Repuesto repuesto;

    @Size(max = 1000)
    @Column(name = "DESCRIPCION", length = 1000)
    private String descripcion;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "CANTIDAD", precision = 12, scale = 2, nullable = false)
    private BigDecimal cantidad = BigDecimal.ZERO;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "PRECIO_UNIT", precision = 12, scale = 2, nullable = false)
    private BigDecimal precioUnit = BigDecimal.ZERO;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "TOTAL_LINEA", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalLinea = BigDecimal.ZERO;

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public OrdenTrabajo getOrden() { return orden; }
    public void setOrden(OrdenTrabajo orden) { this.orden = orden; }

    public TipoDetalle getTipo() { return tipo; }
    public void setTipo(TipoDetalle tipo) { this.tipo = tipo; }

    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }

    public Repuesto getRepuesto() { return repuesto; }
    public void setRepuesto(Repuesto repuesto) { this.repuesto = repuesto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnit() { return precioUnit; }
    public void setPrecioUnit(BigDecimal precioUnit) { this.precioUnit = precioUnit; }

    public BigDecimal getTotalLinea() { return totalLinea; }
    public void setTotalLinea(BigDecimal totalLinea) { this.totalLinea = totalLinea; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DetalleOrden that)) return false;
        return id != null && id.equals(that.id);
    }
    @Override
    public int hashCode() { return 31; }
}
