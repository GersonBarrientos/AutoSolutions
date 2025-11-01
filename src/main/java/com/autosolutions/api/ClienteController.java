package com.autosolutions.api;

import com.autosolutions.domain.Cliente;
import com.autosolutions.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ClienteController {

  private final ClienteService service;

  @GetMapping
  public Page<Cliente> list(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "nombres,asc") String sort,
      @RequestParam(required = false) String q
  ) {
    Sort ordr = Sort.by(sortSplit(sort));
    Pageable pageable = PageRequest.of(page, Math.max(1, size), ordr);

    log.debug("Buscando clientes q='{}' page={} size={}", q, page, size);
    return service.buscar(q, pageable);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Cliente> get(@PathVariable Long id) {
    return service.findById(id)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<Cliente> create(@Valid @RequestBody Cliente body) {
    Cliente saved = service.guardar(body);
    URI loc = URI.create("/api/clientes/" + saved.getId());
    return ResponseEntity.created(loc).body(saved);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Cliente> update(@PathVariable Long id, @Valid @RequestBody Cliente body) {
    
    return service.findById(id)
      .map(existingCliente -> {
          body.setId(id);
          body.setCreatedAt(existingCliente.getCreatedAt());
          body.setVersion(existingCliente.getVersion());
          
          Cliente saved = service.guardar(body);
          return ResponseEntity.ok(saved);
      })
      .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    try {
      service.eliminar(id);
      return ResponseEntity.noContent().build();
    } catch (NoSuchElementException e) {
      return ResponseEntity.notFound().build();
    }
  }

  private Sort.Order[] sortSplit(String sort) {
    String[] parts = sort.split(";");
    return java.util.Arrays.stream(parts)
        .map(s -> {
          String[] p = s.split(",");
          String field = p[0].trim();
          boolean desc = p.length > 1 && p[1].trim().equalsIgnoreCase("desc");
          return new Sort.Order(desc ? Sort.Direction.DESC : Sort.Direction.ASC, field);
        }).toArray(Sort.Order[]::new);
  }
}