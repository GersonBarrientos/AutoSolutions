package com.autosolutions.service;

import com.autosolutions.domain.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ClienteService {
  List<Cliente> listarOrdenado();
  Page<Cliente> buscar(String q, Pageable pageable);
  Optional<Cliente> findById(Long id);
  Cliente guardar(Cliente c);
  void eliminar(Long id);
}
