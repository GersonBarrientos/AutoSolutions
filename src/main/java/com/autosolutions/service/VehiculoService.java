package com.autosolutions.service;

import com.autosolutions.domain.Vehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface VehiculoService {

    Page<Vehiculo> listarPaginado(String q, Pageable pageable);

    List<Vehiculo> listarPorCliente(Long clienteId);

    Optional<Vehiculo> buscarPorId(Long id);

    Vehiculo guardar(Vehiculo vehiculo, Long clienteId, Long marcaId, Long modeloId);

    void eliminar(Long id);
}