package com.autosolutions.repo;

import com.autosolutions.domain.Empleado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

  Page<Empleado> findByNombreIgnoreCaseContainingOrApellidoIgnoreCaseContaining(
      String nombre, String apellido, Pageable pageable);

  boolean existsByEmailIgnoreCase(String email);
  boolean existsByTelefono(String telefono);

  // ✅ Para edición: excluye el propio id
  boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
  boolean existsByTelefonoAndIdNot(String telefono, Long id);
}
