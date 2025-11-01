package com.autosolutions.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ESTADO_ORDEN")
public class EstadoOrden {

    @Id
    @Column(name = "ID")
    private Integer id; // ej: 1=PENDIENTE, 2=EN_PROCESO, 3=FINALIZADA

    @Column(name = "NOMBRE", nullable = false, unique = true, length = 30)
    private String nombre;
}
