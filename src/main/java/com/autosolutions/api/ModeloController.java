// src/main/java/com/autosolutions/api/ModeloController.java
package com.autosolutions.api;

import com.autosolutions.domain.Modelo;
import com.autosolutions.repo.ModeloRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modelos")
public class ModeloController {

  private final ModeloRepository modeloRepo;
  public ModeloController(ModeloRepository modeloRepo) { this.modeloRepo = modeloRepo; }

  public record ModeloDto(Long id, String nombre) {
    static ModeloDto of(Modelo m) { return new ModeloDto(m.getId(), m.getNombre()); }
  }

  @GetMapping("/by-marca/{marcaId}")
  public List<ModeloDto> byMarca(@PathVariable Long marcaId) {
    return modeloRepo.findByMarcaIdOrderByNombreAsc(marcaId)
                     .stream().map(ModeloDto::of).toList();
  }
}
