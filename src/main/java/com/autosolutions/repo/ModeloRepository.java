package com.autosolutions.repo;

import com.autosolutions.domain.Modelo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModeloRepository extends JpaRepository<Modelo, Long> {

    // === Para llenar dropdowns filtrados por marca ===
    List<Modelo> findByMarcaIdOrderByNombreAsc(Long marcaId);

    // === Validación de duplicados (nombre dentro de una marca) ===
    boolean existsByMarcaIdAndNombreIgnoreCase(Long marcaId, String nombre);
}
