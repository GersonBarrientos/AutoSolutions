package com.autosolutions.service.impl;

import com.autosolutions.domain.Cliente;
import com.autosolutions.repo.ClienteRepository;
import com.autosolutions.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

  private final ClienteRepository repo;

  @Override
  @Transactional(readOnly = true)
  public List<Cliente> listarOrdenado() {
    return repo.findAllByOrderByNombresAsc();
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Cliente> buscar(String q, Pageable pageable) {
    if (q == null || q.isBlank()) {
        return repo.findAll(pageable);
    }
    return repo.buscar(q.trim(), pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Cliente> findById(Long id) {
    return repo.findById(id);
  }

  @Override
  public Cliente guardar(Cliente c) {
    // 1. Validar campos obligatorios
    if (c.getNombres() == null || c.getNombres().isBlank()) {
        throw new IllegalArgumentException("Los nombres son obligatorios");
    }
    if (c.getApellidos() == null || c.getApellidos().isBlank()) {
        throw new IllegalArgumentException("Los apellidos son obligatorios");
    }
    if (c.getDui() == null || c.getDui().isBlank()) { // Corregido
        throw new IllegalArgumentException("El DUI es obligatorio"); // Corregido
    }

    // 2. Sanitizar datos
    c.setNombres(c.getNombres().strip());
    c.setApellidos(c.getApellidos().strip());
    c.setDui(c.getDui().strip().toUpperCase()); // Corregido

    if (c.getEmail() != null && !c.getEmail().isBlank()) {
      c.setEmail(c.getEmail().strip().toLowerCase());
    } else {
      c.setEmail(null); 
    }

    // 3. Validación de duplicados
    if (c.getId() == null) {
      // --- Creando ---
      if (repo.existsByDuiIgnoreCase(c.getDui())) { // Corregido
        throw new IllegalArgumentException("Ya existe un cliente con el DUI " + c.getDui()); // Corregido
      }
      if (c.getEmail() != null && repo.existsByEmailIgnoreCase(c.getEmail())) {
        throw new IllegalArgumentException("Ya existe un cliente con el email " + c.getEmail());
      }
    } else {
      // --- Editando ---
      if (repo.existsByDuiIgnoreCaseAndIdNot(c.getDui(), c.getId())) { // Corregido
        throw new IllegalArgumentException("Ya existe OTRO cliente con el DUI " + c.getDui()); // Corregido
      }
      if (c.getEmail() != null && repo.existsByEmailIgnoreCaseAndIdNot(c.getEmail(), c.getId())) {
        throw new IllegalArgumentException("Ya existe OTRO cliente con el email " + c.getEmail());
      }
    }
    
    // 4. Guardar
    return repo.save(c);
  }

  @Override
  public void eliminar(Long id) {
    if (!repo.existsById(id)) {
      throw new NoSuchElementException("El cliente con ID " + id + " no existe");
    }
    repo.deleteById(id);
  }
}
