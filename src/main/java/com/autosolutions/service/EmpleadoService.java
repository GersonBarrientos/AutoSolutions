package com.autosolutions.service;

import com.autosolutions.domain.Empleado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EmpleadoService {
  Page<Empleado> listar(String q, Pageable pageable);
  Optional<Empleado> buscar(Long id);
  Empleado crear(Empleado e);
  Empleado actualizar(Long id, Empleado e);
  void eliminar(Long id);
}
