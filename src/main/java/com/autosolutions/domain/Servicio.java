package com.autosolutions.domain;

import com.autosolutions.jpa.Boolean01Converter;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "SERVICIO")
public class Servicio {

    @Id
    @SequenceGenerator(
        name = "seqServicio",
        sequenceName = "GERSON.SERVICIO_SEQ", // Ajusta el esquema si fuese necesario
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqServicio")
    @Column(name = "ID")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "Máximo 100 caracteres")
    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @DecimalMin(value = "0.00", message = "El precio no puede ser negativo")
    @Column(name = "PRECIO_UNIT", nullable = false, precision = 19, scale = 2)
    private BigDecimal precioUnit = BigDecimal.ZERO;

    @Convert(converter = Boolean01Converter.class) // Soporta NUMBER(1) ↔ boolean
    @Column(name = "ACTIVO", nullable = false, precision = 1, scale = 0)
    private Boolean activo = Boolean.TRUE;

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getPrecioUnit() { return precioUnit; }
    public void setPrecioUnit(BigDecimal precioUnit) { this.precioUnit = precioUnit; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
