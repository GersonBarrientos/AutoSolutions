package com.autosolutions.service.impl;

import com.autosolutions.domain.Repuesto;
import com.autosolutions.repo.RepuestoRepository;
import com.autosolutions.service.RepuestoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class RepuestoServiceImpl implements RepuestoService {

    private final RepuestoRepository repository;

    public RepuestoServiceImpl(RepuestoRepository repository) {
        this.repository = repository;
    }

    @Override @Transactional(readOnly = true)
    public List<Repuesto> listar(String q) {
        return (q == null || q.isBlank())
            ? repository.findAll()
            : repository.findByNombreContainingIgnoreCase(q.trim(), Pageable.unpaged()).getContent();
    }

    @Override @Transactional(readOnly = true)
    public Page<Repuesto> listar(String q, Pageable pageable) {
        return (q == null || q.isBlank())
            ? repository.findAll(pageable)
            : repository.findByNombreContainingIgnoreCase(q.trim(), pageable);
    }

    @Override @Transactional(readOnly = true)
    public Optional<Repuesto> buscarPorId(Long id) { return repository.findById(id); }

    private static String normalizeKey(String s) {
        if (s == null) return null;
        String t = s.strip().toLowerCase();
        t = Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return t.replaceAll("\\s+", " ");
    }

    @Override
    public Repuesto guardar(Repuesto r) {
        if (r.getNombre() == null || r.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");
        r.setNombre(r.getNombre().strip());

        if (r.getPrecioUnit() == null || r.getPrecioUnit().signum() < 0)
            throw new IllegalArgumentException("El precio no puede ser negativo");
        if (r.getStock() == null || r.getStock().signum() < 0)
            throw new IllegalArgumentException("El stock no puede ser negativo");

        if (r.getId() == null) {
            if (repository.existsByNombreIgnoreCase(r.getNombre()))
                throw new IllegalArgumentException("Ya existe un repuesto con ese nombre");
        } else {
            if (repository.existsByNombreIgnoreCaseAndIdNot(r.getNombre(), r.getId()))
                throw new IllegalArgumentException("Ya existe otro repuesto con ese nombre");
        }

        return repository.save(r);
    }

    @Override
    public void eliminar(Long id) {
        if (!repository.existsById(id)) throw new NoSuchElementException("El repuesto no existe");
        repository.deleteById(id);
    }

    @Override
    public Repuesto toggleActivo(Long id) {
        Repuesto r = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Repuesto no encontrado"));
        r.setActivo(Boolean.TRUE.equals(r.getActivo()) ? Boolean.FALSE : Boolean.TRUE);
        return repository.save(r);
    }

    @Override
    public Repuesto incrementarStock(Long id, BigDecimal cantidad) {
        if (cantidad == null || cantidad.signum() <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }
        Repuesto r = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Repuesto no encontrado"));
        r.setStock(r.getStock().add(cantidad));
        return repository.save(r);
    }

    @Override
    public Repuesto decrementarStock(Long id, BigDecimal cantidad) {
        if (cantidad == null || cantidad.signum() <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }
        Repuesto r = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Repuesto no encontrado"));
        BigDecimal nuevo = r.getStock().subtract(cantidad);
        if (nuevo.signum() < 0) {
            throw new IllegalStateException("No hay stock suficiente");
        }
        r.setStock(nuevo);
        return repository.save(r);
    }

    @Override
    public int importarCsv(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Archivo CSV vacío");
        }
        int count = 0;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line; boolean header = true;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String lower = line.toLowerCase();
                if (header && (lower.contains("nombre;") || lower.startsWith("id;"))) {
                    header = false; // saltar encabezado
                    continue;
                }
                String[] parts = line.split(";");
                if (parts.length < 4) continue;
                String nombre = parts[0].trim();
                String precio = parts[1].trim().replace(",", ".");
                String stock  = parts[2].trim().replace(",", ".");
                String activoStr = parts[3].trim().toLowerCase();

                Repuesto r = new Repuesto();
                r.setNombre(nombre);
                r.setPrecioUnit(new BigDecimal(precio));
                r.setStock(new BigDecimal(stock));
                r.setActivo("1".equals(activoStr) || "true".equals(activoStr) || "si".equals(activoStr));

                try {
                    guardar(r);
                    count++;
                } catch (Exception ignore) {
                    // duplicados/validaciones: continuar con el siguiente
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al leer CSV: " + e.getMessage());
        }
        return count;
    }
}
