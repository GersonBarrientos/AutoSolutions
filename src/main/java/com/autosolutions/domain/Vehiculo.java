package com.autosolutions.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehiculo",
       uniqueConstraints = {
         @UniqueConstraint(name = "UK_VEHICULO_PLACA", columnNames = "placa")
       })
public class Vehiculo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Version
  private Long version;

  // ---------- Relaciones ----------
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "cliente_id", nullable = false,
              foreignKey = @ForeignKey(name = "FK_VEHICULO_CLIENTE"))
  private Cliente cliente;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "marca_id",
              foreignKey = @ForeignKey(name = "FK_VEHICULO_MARCA"))
  private Marca marca;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "modelo_id",
              foreignKey = @ForeignKey(name = "FK_VEHICULO_MODELO"))
  private Modelo modelo;

  // ---------- Campos ----------
  @NotBlank(message = "La placa es obligatoria.")
  @Size(max = 20, message = "Máximo 20 caracteres.")
  @Column(name = "placa", nullable = false, length = 20)
  private String placa;

  // ✅ CORRECCIÓN: nada de @PastOrPresent aquí.
  // Usamos un rango numérico coherente con tu <input min/max> del form.
  @Min(value = 1900, message = "El año mínimo es 1900.")
  @Max(value = 2100, message = "El año máximo es 2100.")
  @Column(name = "anio")
  private Integer anio;

  @Size(max = 40, message = "Máximo 40 caracteres.")
  @Column(name = "color", length = 40)
  private String color;

  @Size(max = 40, message = "Máximo 40 caracteres.")
  @Column(name = "vin", length = 40)
  private String vin;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // ---------- Getters/Setters ----------
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Long getVersion() { return version; }
  public void setVersion(Long version) { this.version = version; }

  public Cliente getCliente() { return cliente; }
  public void setCliente(Cliente cliente) { this.cliente = cliente; }

  public Marca getMarca() { return marca; }
  public void setMarca(Marca marca) { this.marca = marca; }

  public Modelo getModelo() { return modelo; }
  public void setModelo(Modelo modelo) { this.modelo = modelo; }

  public String getPlaca() { return placa; }
  public void setPlaca(String placa) { this.placa = placa; }

  public Integer getAnio() { return anio; }
  public void setAnio(Integer anio) { this.anio = anio; }

  public String getColor() { return color; }
  public void setColor(String color) { this.color = color; }

  public String getVin() { return vin; }
  public void setVin(String vin) { this.vin = vin; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
