package com.autosolutions.service;

import com.autosolutions.domain.Repuesto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RepuestoService {
    List<Repuesto> listar(String q);
    Page<Repuesto> listar(String q, Pageable pageable);
    Optional<Repuesto> buscarPorId(Long id);
    Repuesto guardar(Repuesto r);
    void eliminar(Long id);
    Repuesto toggleActivo(Long id);
    Repuesto incrementarStock(Long id, BigDecimal cantidad);
    Repuesto decrementarStock(Long id, BigDecimal cantidad);

    /** Importa CSV: nombre;precio;stock;activo */
    int importarCsv(MultipartFile file);
}
