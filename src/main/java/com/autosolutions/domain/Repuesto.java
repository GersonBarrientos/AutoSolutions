package com.autosolutions.domain;

import com.autosolutions.jpa.Boolean01Converter;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "REPUESTO")
public class Repuesto {

    @Id
    @SequenceGenerator(
        name = "seqRepuesto",
        sequenceName = "GERSON.REPUESTO_SEQ", // ajusta el esquema si aplica
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqRepuesto")
    @Column(name = "ID")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "Máximo 120 caracteres")
    @Column(name = "NOMBRE", nullable = false, length = 120)
    private String nombre;

    @DecimalMin(value = "0.00", message = "El precio no puede ser negativo")
    @Column(name = "PRECIO_UNIT", nullable = false, precision = 19, scale = 2)
    private BigDecimal precioUnit = BigDecimal.ZERO;

    @DecimalMin(value = "0.00", message = "El stock no puede ser negativo")
    @Column(name = "STOCK", nullable = false, precision = 19, scale = 2)
    private BigDecimal stock = BigDecimal.ZERO;

    @Convert(converter = Boolean01Converter.class)
    @Column(name = "ACTIVO", nullable = false, precision = 1, scale = 0)
    private Boolean activo = Boolean.TRUE;

    @Version
    @Column(name = "VERSION")
    private Long version;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getPrecioUnit() { return precioUnit; }
    public void setPrecioUnit(BigDecimal precioUnit) { this.precioUnit = precioUnit; }
    public BigDecimal getStock() { return stock; }
    public void setStock(BigDecimal stock) { this.stock = stock; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
