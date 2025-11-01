package com.autosolutions.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "MARCA")
public class Marca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // O SEQUENCE si prefieres consistencia con Cliente/Servicio
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 50, unique = true)
    private String nombre;

    // Podrías añadir más campos si fuese necesario, como país de origen, etc.
}