package com.autosolutions.service;

import com.autosolutions.domain.Servicio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ServicioService {
    // Lista completa (sin paginación)
    List<Servicio> listar(String q);

    // Lista paginada
    Page<Servicio> listar(String q, Pageable pageable);

    Optional<Servicio> buscarPorId(Long id);

    Servicio guardar(Servicio s);

    void eliminar(Long id);

    Servicio toggleActivo(Long id);
}
