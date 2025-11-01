package com.autosolutions.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "PAGO")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pago {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pago_seq")
  @SequenceGenerator(name = "pago_seq", sequenceName = "PAGO_SEQ", allocationSize = 1)
  @Column(name = "ID")
  @EqualsAndHashCode.Include
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "ORDEN_ID", nullable = false)
  @JsonBackReference
  private OrdenTrabajo orden;

  @Column(name = "FECHA_PAGO", nullable = false)
  private LocalDateTime fechaPago;

  @Column(name = "MONTO", nullable = false)
  private BigDecimal monto;

  @Column(name = "METODO", nullable = false, length = 20)
  private String metodo; // EFECTIVO/TARJETA/TRANSFERENCIA/OTRO

  @Column(name = "REFERENCIA", length = 60)
  private String referencia;
}
