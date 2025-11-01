package com.autosolutions.repo;

import com.autosolutions.domain.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

  // Lista ordenada por nombres ASC
  List<Cliente> findAllByOrderByNombresAsc();

  // Búsqueda por varias columnas (corregido a 'dui')
  @Query("""
         select c from Cliente c
         where lower(c.nombres)   like lower(concat('%', :q, '%'))
            or lower(c.apellidos) like lower(concat('%', :q, '%'))
            or lower(c.dui)       like lower(concat('%', :q, '%'))
            or lower(c.telefono)  like lower(concat('%', :q, '%'))
            or lower(c.email)     like lower(concat('%', :q, '%'))
         """)
  Page<Cliente> buscar(String q, Pageable pageable);

  // --- Métodos para validación de duplicados (corregidos a 'dui') ---

  boolean existsByDuiIgnoreCase(String dui);
  boolean existsByDuiIgnoreCaseAndIdNot(String dui, Long id);

  boolean existsByEmailIgnoreCase(String email);
  boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}