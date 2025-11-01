package com.autosolutions.domain;

import com.autosolutions.jpa.Boolean01Converter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "CLIENTE", schema = "GERSON")
public class Cliente {

  // ... (otros campos: id, nombres, apellidos, telefono, email, direccion, dui, activo) ...
    @Id
    @SequenceGenerator(name = "seqCliente", sequenceName = "GERSON.CLIENTE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqCliente")
    @Column(name = "ID")
    private Long id;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 120, message = "Máximo 120 caracteres")
    @Column(name = "NOMBRES", nullable = false, length = 120)
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 120, message = "Máximo 120 caracteres")
    @Column(name = "APELLIDOS", length = 120)
    private String apellidos;

    @Size(max = 25, message = "Máximo 25 caracteres")
    @Column(name = "TELEFONO", length = 25)
    private String telefono;

    @Email(message = "Debe ser un correo electrónico válido")
    @Size(max = 180, message = "Máximo 180 caracteres")
    @Column(name = "EMAIL", length = 180)
    private String email;

    @Size(max = 300, message = "Máximo 300 caracteres")
    @Column(name = "DIRECCION", length = 300)
    private String direccion;

    @NotBlank(message = "El DUI es obligatorio")
    @Size(max = 20, message = "Máximo 20 caracteres")
    @Column(name = "DUI", length = 20)
    private String dui;

    @Convert(converter = Boolean01Converter.class)
    @Column(name = "ACTIVO", nullable = false, precision = 1, scale = 0)
    private Boolean activo = Boolean.TRUE;

  // --- CORRECCIÓN AQUÍ ---
  @Column(name = "CREATED_AT", updatable = false) // <--- Añadir updatable = false
  private OffsetDateTime createdAt;

  @Column(name = "UPDATED_AT") // Este SÍ se actualiza (por @PreUpdate)
  private OffsetDateTime updatedAt;

  @Version
  @Column(name = "VERSION") // @Version implícitamente maneja su actualización
  private Long version;

  @PrePersist
  public void prePersist() {
    var now = OffsetDateTime.now();
    // Solo se establecen al crear
    this.createdAt = now;
    this.updatedAt = now;
    if (this.activo == null) this.activo = Boolean.TRUE;
    // La versión inicial la maneja JPA, usualmente es 0 o 1
  }

  @PreUpdate
  public void preUpdate() {
    // Solo se actualiza updatedAt
    this.updatedAt = OffsetDateTime.now();
    // La versión la incrementa JPA automáticamente
  }

  // ... (getNombreCompleto helper) ...
   public String getNombreCompleto() {
    String n = nombres != null ? nombres.trim() : "";
    String a = apellidos != null ? apellidos.trim() : "";
    return (n + " " + a).trim();
  }
}