package com.autosolutions.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "DETALLE_ORDEN")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DetalleOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORDEN_ID", nullable = false)
    @JsonBackReference
    private OrdenTrabajo orden;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 20)
    private TipoDetalle tipo; // SERVICIO o REPUESTO

    // Solo aplica si es REPUESTO
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPUESTO_ID")
    private Repuesto repuesto;

    @Column(name = "DESCRIPCION", length = 300)
    private String descripcion;

    @Column(name = "CANTIDAD", nullable = false, precision = 14, scale = 2)
    private BigDecimal cantidad;

    @Column(name = "PRECIO_UNIT", nullable = false, precision = 14, scale = 2)
    private BigDecimal precioUnit;

    @Column(name = "TOTAL_LINEA", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalLinea;
}
