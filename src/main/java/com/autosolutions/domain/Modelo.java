package com.autosolutions.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "MODELO")
public class Modelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // O SEQUENCE
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MARCA_ID", nullable = false)
    private Marca marca;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    // Constraint único para evitar 'Toyota Corolla' y 'Toyota corolla' duplicados (si la BD lo soporta)
    // @Table(uniqueConstraints = @UniqueConstraint(columnNames = {"MARCA_ID", "NOMBRE"}))
}