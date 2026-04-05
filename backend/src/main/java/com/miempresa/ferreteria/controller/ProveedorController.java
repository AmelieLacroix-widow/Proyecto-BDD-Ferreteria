package com.miempresa.ferreteria.controller;

import com.miempresa.ferreteria.model.Proveedor;
import com.miempresa.ferreteria.service.ProveedorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proveedores")
public class ProveedorController {

    private final ProveedorService service;

    public ProveedorController(ProveedorService service) {
        this.service = service;
    }

    // GET /proveedores
    @GetMapping
    public List<Proveedor> listar() {
        return service.todos();
    }

    // GET /proveedores/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> obtener(@PathVariable Integer id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /proveedores/buscar?nombre=...
    @GetMapping("/buscar")
    public List<Proveedor> buscar(@RequestParam String nombre) {
        return service.buscar(nombre);
    }

    // POST /proveedores
    @PostMapping
    public ResponseEntity<Proveedor> crear(@RequestBody Proveedor proveedor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(proveedor));
    }

    // PUT /proveedores/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizar(@PathVariable Integer id,
                                                @RequestBody Proveedor proveedor) {
        return service.buscarPorId(id).map(existing -> {
            proveedor.setIdProveedor(id);
            return ResponseEntity.ok(service.guardar(proveedor));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /proveedores/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (service.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
