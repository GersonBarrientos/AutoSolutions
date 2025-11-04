package com.autosolutions.repo;

import com.autosolutions.domain.OrdenTrabajo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Long> {

    /**
     * Lista rápida sin joins pesados.
     */
    List<OrdenTrabajo> findAllByOrderByIdDesc();

    /**
     * Lista con fetch de vehiculo y estado para que no dé LazyInitialization en la vista/lista.
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
     * Ejemplo de conteo de órdenes activas por estado (ids como Long).
     */
    Long countByEstadoIdIn(List<Long> estadoIds);

    /**
     * Cargar una orden con sus detalles y repuestos (evita Lazy en editar/ver).
     * Usamos JOIN FETCH por id para asegurar un solo query completo.
     */
    @Query("""
        select ot
        from OrdenTrabajo ot
          left join fetch ot.vehiculo v
          left join fetch ot.estado e
          left join fetch ot.detalles d
          left join fetch d.repuesto r
        where ot.id = :id
    """)
    OrdenTrabajo findWithDetallesById(@Param("id") Long id);

    // Alternativa con EntityGraph (si prefieres):
    // @EntityGraph(attributePaths = {"vehiculo","estado","detalles","detalles.repuesto"})
    // Optional<OrdenTrabajo> findById(Long id);
}
