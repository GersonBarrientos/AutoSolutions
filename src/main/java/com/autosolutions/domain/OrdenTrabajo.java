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
@Table(name = "ORDEN_TRABAJO")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrdenTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ajusta a tu secuencia si usas secuencia en Oracle
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

    // ⬇⬇⬇ AQUÍ EL CAMBIO IMPORTANTE
    @Column(name = "FECHA_SALIDA_ESTIMADA") // <- antes estaba FECHA_SALIDA_EST
    private LocalDateTime fechaSalidaEstimada;

    @Column(name = "DIAGNOSTICO", length = 1000)
    private String diagnostico;

    @Column(name = "OBSERVACIONES", length = 1000)
    private String observaciones;

    @Builder.Default
    @Column(name = "TOTAL", nullable = false, precision = 14, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Builder.Default
    @OneToMany(
            mappedBy = "orden",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<DetalleOrden> detalles = new HashSet<>();
}
