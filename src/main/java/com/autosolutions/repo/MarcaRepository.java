package com.autosolutions.repo;

import com.autosolutions.domain.Marca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarcaRepository extends JpaRepository<Marca, Long> {

    // === Para llenar dropdowns ordenados alfabéticamente ===
    List<Marca> findAllByOrderByNombreAsc();

    // === Validación de duplicados ===
    boolean existsByNombreIgnoreCase(String nombre);
}
