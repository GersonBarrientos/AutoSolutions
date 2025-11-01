package com.autosolutions.repo;

import com.autosolutions.domain.OrdenTrabajo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Long> {

    /**
     * Lista rápida sin joins pesados.
     */
    List<OrdenTrabajo> findAllByOrderByIdDesc();

    /**
     * Lista con fetch de vehiculo y estado para que no dé LazyInitialization
     * en la vista/lista.
     */
    @Query("""
        select ot
        from OrdenTrabajo ot
          join fetch ot.vehiculo v
          join fetch ot.estado e
        order by ot.id desc
    """)
    List<OrdenTrabajo> findAllWithVehiculoEstadoOrderByIdDesc();

    /**
     * Ejemplo de conteo de órdenes activas por estado.
     * Sirve para un dashboard si quieres.
     */
    Long countByEstadoIdIn(List<Integer> estadoIds);

    /**
     * Cargar una orden con sus detalles, repuestos, etc.
     * Esto es útil para editar/ver sin reventar Lazy.
     */
    @EntityGraph(attributePaths = {
            "vehiculo",
            "estado",
            "detalles",
            "detalles.repuesto"
    })
    OrdenTrabajo findWithDetallesById(Long id);
}
