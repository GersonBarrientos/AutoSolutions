package com.autosolutions.service.impl;

import com.autosolutions.domain.Cliente;
import com.autosolutions.domain.Marca;
import com.autosolutions.domain.Modelo;
import com.autosolutions.domain.Vehiculo;
import com.autosolutions.repo.ClienteRepository;
import com.autosolutions.repo.MarcaRepository;
import com.autosolutions.repo.ModeloRepository;
import com.autosolutions.repo.VehiculoRepository;
import com.autosolutions.service.VehiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository vehiculoRepo;
    private final ClienteRepository clienteRepo;
    private final MarcaRepository marcaRepo;
    private final ModeloRepository modeloRepo;

    @Override
    @Transactional(readOnly = true)
    public Page<Vehiculo> listarPaginado(String q, Pageable pageable) {
        String term = (StringUtils.hasText(q) ? q.trim() : null);
        return (term == null) ? vehiculoRepo.findAll(pageable) : vehiculoRepo.search(term, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehiculo> listarPorCliente(Long clienteId) {
        return vehiculoRepo.findByClienteIdOrderByPlacaAsc(clienteId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Vehiculo> buscarPorId(Long id) {
        return vehiculoRepo.findById(id);
    }

    @Override
    public Vehiculo guardar(Vehiculo vehiculo, Long clienteId, Long marcaId, Long modeloId) {
        // 1) Validaciones de entrada
        if (clienteId == null) {
            throw new IllegalArgumentException("El cliente es obligatorio.");
        }
        if (!StringUtils.hasText(vehiculo.getPlaca())) {
            throw new IllegalArgumentException("La placa es obligatoria.");
        }

        // 2) Normalización
        vehiculo.setPlaca(vehiculo.getPlaca().trim().toUpperCase());
        if (vehiculo.getColor() != null) vehiculo.setColor(vehiculo.getColor().trim());
        if (vehiculo.getVin() != null) vehiculo.setVin(vehiculo.getVin().trim().toUpperCase());

        // 3) Relación obligatoria: Cliente
        Cliente cliente = clienteRepo.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado (ID: " + clienteId + ")"));
        vehiculo.setCliente(cliente);

        // 4) Relaciones opcionales: Marca / Modelo (modelo debe pertenecer a la marca)
        Marca marca = null;
        if (marcaId != null) {
            marca = marcaRepo.findById(marcaId)
                    .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada (ID: " + marcaId + ")"));
        }
        vehiculo.setMarca(marca);

        Modelo modelo = null;
        if (modeloId != null) {
            Modelo encontrado = modeloRepo.findById(modeloId)
                    .orElseThrow(() -> new IllegalArgumentException("Modelo no encontrado (ID: " + modeloId + ")"));
            if (marca == null || encontrado.getMarca() == null || !encontrado.getMarca().getId().equals(marca.getId())) {
                throw new IllegalArgumentException("El modelo seleccionado no pertenece a la marca indicada.");
            }
            modelo = encontrado;
        }
        vehiculo.setModelo(modelo);

        // 5) Unicidad de placa
        boolean duplicado = (vehiculo.getId() == null)
                ? vehiculoRepo.existsByPlacaIgnoreCase(vehiculo.getPlaca())
                : vehiculoRepo.existsByPlacaIgnoreCaseAndIdNot(vehiculo.getPlaca(), vehiculo.getId());
        if (duplicado) {
            throw new IllegalArgumentException("Ya existe un vehículo con la placa " + vehiculo.getPlaca());
        }

        // 6) Guardar (create/update)
        return vehiculoRepo.save(vehiculo);
    }

    @Override
    public void eliminar(Long id) {
        if (!vehiculoRepo.existsById(id)) {
            throw new NoSuchElementException("Vehículo no encontrado (ID: " + id + ")");
        }
        try {
            vehiculoRepo.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // Ordenes de trabajo u otras FKs
            throw new IllegalStateException(
                    "No se puede eliminar el vehículo (ID: " + id + ") porque tiene registros asociados.", e);
        }
    }
}
