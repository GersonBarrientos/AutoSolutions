package com.autosolutions.api.dto;

import lombok.Data;

@Data
public class VehiculoDTO {
  private Long clienteId;
  private String placa;
  private String marca;
  private String modelo;
  private Integer anio;
  private String color;
  private String vin;
}
