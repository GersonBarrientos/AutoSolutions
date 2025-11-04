package com.autosolutions.repo;

import com.autosolutions.domain.OrdenTrabajo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Long> {

    @Query("""
           select ot
           from OrdenTrabajo ot
           left join fetch ot.vehiculo v
           left join fetch ot.estado e
           order by ot.id desc
           """)
    List<OrdenTrabajo> findAllWithVehiculoEstadoOrderByIdDesc();

    @Query("""
           select ot
           from OrdenTrabajo ot
             left join fetch ot.vehiculo v
             left join fetch ot.estado e
             left join fetch ot.detalles d
             left join fetch d.repuesto r
             left join fetch d.servicio s
           where ot.id = :id
           """)
    Optional<OrdenTrabajo> findWithDetallesById(Long id);

    @EntityGraph(attributePaths = {"vehiculo","estado","detalles","detalles.repuesto","detalles.servicio"})
    Optional<OrdenTrabajo> findById(Long id);

    // <<< NUEVO: usado por HomeController para contar órdenes activas >>>
    long countByEstado_IdIn(Collection<Integer> estadoIds);
}
