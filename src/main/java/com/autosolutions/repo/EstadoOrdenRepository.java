package com.autosolutions.repo;

import com.autosolutions.domain.EstadoOrden;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoOrdenRepository extends JpaRepository<EstadoOrden, Integer> {
}
