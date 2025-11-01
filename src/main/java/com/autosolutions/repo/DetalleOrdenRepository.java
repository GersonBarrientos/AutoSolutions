package com.autosolutions.repo;

import com.autosolutions.domain.DetalleOrden;
import com.autosolutions.domain.TipoDetalle;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {

    @EntityGraph(attributePaths = {"repuesto"})
    List<DetalleOrden> findByOrdenIdOrderByIdAsc(Long ordenId);

    @EntityGraph(attributePaths = {"repuesto"})
    List<DetalleOrden> findByOrdenIdAndTipoOrderByIdAsc(Long ordenId, TipoDetalle tipo);
}
