package com.autosolutions.repo;

import com.autosolutions.domain.Vehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    // === Carga paginada con relaciones (para listas) ===
    @EntityGraph(attributePaths = {"cliente", "marca", "modelo"})
    @NonNull
    Page<Vehiculo> findAll(@NonNull Pageable pageable);

    // === Búsqueda general y avanzada ===
    @Query("""
      SELECT v FROM Vehiculo v
       LEFT JOIN v.cliente c
       LEFT JOIN v.marca m
       LEFT JOIN v.modelo mo
      WHERE (:q IS NULL OR :q = ''
         OR LOWER(v.placa) LIKE LOWER(CONCAT('%', :q, '%'))
         OR LOWER(v.color) LIKE LOWER(CONCAT('%', :q, '%'))
         OR LOWER(v.vin)   LIKE LOWER(CONCAT('%', :q, '%'))
         OR (c IS NOT NULL AND (LOWER(c.nombres) LIKE LOWER(CONCAT('%', :q, '%'))
                             OR LOWER(c.apellidos) LIKE LOWER(CONCAT('%', :q, '%'))))
         OR (m IS NOT NULL AND LOWER(m.nombre) LIKE LOWER(CONCAT('%', :q, '%')))
         OR (mo IS NOT NULL AND LOWER(mo.nombre) LIKE LOWER(CONCAT('%', :q, '%')))
      )
    """)
    Page<Vehiculo> search(@Param("q") String q, Pageable pageable);

    // === Vehículos por cliente ===
    @EntityGraph(attributePaths = {"cliente", "marca", "modelo"})
    List<Vehiculo> findByClienteIdOrderByPlacaAsc(Long clienteId);

    // === Validaciones ===
    boolean existsByPlacaIgnoreCase(String placa);
    boolean existsByPlacaIgnoreCaseAndIdNot(String placa, Long id);

    // === Carga individual (con relaciones) ===
    @EntityGraph(attributePaths = {"cliente", "marca", "modelo"})
    @NonNull
    Optional<Vehiculo> findById(@NonNull Long id);
}
