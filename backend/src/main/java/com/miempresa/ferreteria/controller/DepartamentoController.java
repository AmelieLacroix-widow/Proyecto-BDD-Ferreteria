package com.miempresa.ferreteria.controller;

import com.miempresa.ferreteria.model.Departamento;
import com.miempresa.ferreteria.service.DepartamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departamentos")
public class DepartamentoController {

    private final DepartamentoService service;

    public DepartamentoController(DepartamentoService service) {
        this.service = service;
    }

    // GET /departamentos
    @GetMapping
    public List<Departamento> listar() {
        return service.todos();
    }

    // GET /departamentos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Departamento> obtener(@PathVariable Integer id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /departamentos
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Departamento departamento) {
        if (service.existe(departamento.getNombreDepartamento())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe un departamento con ese nombre.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(departamento));
    }

    // PUT /departamentos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Departamento> actualizar(@PathVariable Integer id,
                                                   @RequestBody Departamento departamento) {
        return service.buscarPorId(id).map(existing -> {
            departamento.setIdDepartamento(id);
            return ResponseEntity.ok(service.guardar(departamento));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /departamentos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (service.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
