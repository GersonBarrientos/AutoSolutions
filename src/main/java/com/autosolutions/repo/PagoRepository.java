package com.autosolutions.repo;

import com.autosolutions.domain.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
  List<Pago> findByOrdenId(Long ordenId);
}
