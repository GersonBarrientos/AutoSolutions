package com.autosolutions.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "empleado",
  uniqueConstraints = {
    @UniqueConstraint(name = "UK_EMPLEADO_EMAIL", columnNames = "email"),
    @UniqueConstraint(name = "UK_EMPLEADO_TELEFONO", columnNames = "telefono")
  }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Empleado {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Nombre es obligatorio")
  @Column(nullable = false, length = 100)
  private String nombre;

  @NotBlank(message = "Apellido es obligatorio")
  @Column(nullable = false, length = 100)
  private String apellido;

  @Email(message = "Email no válido")
  @NotBlank(message = "Email es obligatorio")
  @Column(nullable = false, length = 150)
  private String email;

  @NotBlank(message = "Teléfono es obligatorio")
  @Column(nullable = false, length = 30)
  private String telefono;

  @Column(length = 120)
  private String especialidad;

  // ✅ Ahora puede ser null (coincide con V11)
  @Column(name = "fecha_ingreso", nullable = true)
  private LocalDate fechaIngreso;

  @Column(nullable = false)
  private boolean activo = true;

  @Transient
  public String getNombreCompleto() {
    return (nombre == null ? "" : nombre) + " " + (apellido == null ? "" : apellido);
  }
}
