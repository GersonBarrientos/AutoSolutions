package com.autosolutions.service.impl;

import com.autosolutions.domain.Empleado;
import com.autosolutions.repo.EmpleadoRepository;
import com.autosolutions.service.EmpleadoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmpleadoServiceImpl implements EmpleadoService {

  private final EmpleadoRepository repo;

  @Override
  public Page<Empleado> listar(String q, Pageable pageable) {
    if (q == null || q.isBlank()) {
      return repo.findAll(pageable);
    }
    return repo.findByNombreIgnoreCaseContainingOrApellidoIgnoreCaseContaining(q, q, pageable);
  }

  @Override
  public Optional<Empleado> buscar(Long id) {
    return repo.findById(id);
  }

  @Transactional
  @Override
  public Empleado crear(Empleado e) {
    // Duplicados
    if (repo.existsByEmailIgnoreCase(e.getEmail()))
      throw new DataIntegrityViolationException("Email duplicado");
    if (repo.existsByTelefono(e.getTelefono()))
      throw new DataIntegrityViolationException("Teléfono duplicado");

    return repo.save(e);
  }

  @Transactional
  @Override
  public Empleado actualizar(Long id, Empleado e) {
    Empleado db = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado"));

    // Duplicados (excluye el propio id)
    if (repo.existsByEmailIgnoreCaseAndIdNot(e.getEmail(), id))
      throw new DataIntegrityViolationException("Email duplicado");
    if (repo.existsByTelefonoAndIdNot(e.getTelefono(), id))
      throw new DataIntegrityViolationException("Teléfono duplicado");

    db.setNombre(e.getNombre());
    db.setApellido(e.getApellido());
    db.setEmail(e.getEmail());
    db.setTelefono(e.getTelefono());
    db.setEspecialidad(e.getEspecialidad());
    db.setFechaIngreso(e.getFechaIngreso()); // puede venir null
    db.setActivo(e.isActivo());
    return repo.save(db);
  }

  @Transactional
  @Override
  public void eliminar(Long id) {
    if (!repo.existsById(id)) return;
    repo.deleteById(id);
  }
}
