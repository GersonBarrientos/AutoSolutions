package com.autosolutions.service.impl;

import com.autosolutions.domain.Servicio;
import com.autosolutions.repo.ServicioRepository;
import com.autosolutions.service.ServicioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository repository;

    public ServicioServiceImpl(ServicioRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Servicio> listar(String q) {
        if (q == null || q.isBlank()) {
            return repository.findAll();
        }
        // degradado simple a Page 0..Integer.MAX_VALUE si quisieras unificar (no necesario)
        return repository.findByNombreContainingIgnoreCase(q.trim(), Pageable.unpaged()).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Servicio> listar(String q, Pageable pageable) {
        if (q == null || q.isBlank()) {
            return repository.findAll(pageable);
        }
        return repository.findByNombreContainingIgnoreCase(q.trim(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Servicio> buscarPorId(Long id) {
        return repository.findById(id);
    }

    private static String normalizeKey(String s) {
        if (s == null) return null;
        String t = s.strip().toLowerCase();
        // quitar tildes y normalizar espacios
        t = Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        t = t.replaceAll("\\s+", " ");
        return t;
    }

    @Override
    public Servicio guardar(Servicio s) {
        // Normaliza el nombre para comparar duplicados
        String key = normalizeKey(s.getNombre());
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        // Estrategia simple: usar existsBy* y adicionalmente revisar normalización
        if (s.getId() == null) {
            // Nuevo
            if (repository.existsByNombreIgnoreCase(s.getNombre().strip())) {
                throw new IllegalArgumentException("Ya existe un servicio con ese nombre");
            }
        } else {
            // Edición
            if (repository.existsByNombreIgnoreCaseAndIdNot(s.getNombre().strip(), s.getId())) {
                throw new IllegalArgumentException("Ya existe otro servicio con ese nombre");
            }
        }

        // Sanitiza antes de guardar
        s.setNombre(s.getNombre().strip());
        return repository.save(s);
    }

    @Override
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("El servicio no existe");
        }
        repository.deleteById(id);
    }

    @Override
    public Servicio toggleActivo(Long id) {
        Servicio s = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Servicio no encontrado"));
        s.setActivo(Boolean.TRUE.equals(s.getActivo()) ? Boolean.FALSE : Boolean.TRUE);
        return repository.save(s);
    }
}
