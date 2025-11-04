package com.autosolutions.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ORDEN_TRABAJO", schema = "GERSON")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrdenTrabajo {

    @Id
    // Usa IDENTITY solo si la columna ID es IDENTITY en Oracle 21c+.
    // Si usas secuencia, cambia a @SequenceGenerator + @GeneratedValue(strategy = SEQUENCE, generator = "...")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "VEHICULO_ID", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ESTADO_ID", nullable = false)
    private EstadoOrden estado;

    @Column(name = "FECHA_INGRESO", nullable = false)
    private LocalDateTime fechaIngreso;

    // <-- clave: usar el nombre real de la columna
    @Column(name = "FECHA_SALIDA")
    private LocalDateTime fechaSalidaEstimada;

    @Column(name = "KM_INGRESO", precision = 10)
    private Long kmIngreso;

    @Column(name = "DIAGNOSTICO", length = 1000)
    private String diagnostico;

    @Column(name = "OBSERVACIONES", length = 1000)
    private String observaciones;

    // Si manejas empleado en tu modelo:
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMPLEADO_ID")
    private Empleado empleado;

    @Column(name = "SUBTOTAL_MO", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal subtotalManoObra = BigDecimal.ZERO;

    @Column(name = "SUBTOTAL_REP", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal subtotalRepuestos = BigDecimal.ZERO;

    @Column(name = "DESCUENTO", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(name = "IMPUESTO", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal impuesto = BigDecimal.ZERO;

    // En la BD es NUMBER(19,4); ajusta tu mapeo
    @Column(name = "TOTAL", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(
            mappedBy = "orden",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<DetalleOrden> detalles = new HashSet<>();
}
