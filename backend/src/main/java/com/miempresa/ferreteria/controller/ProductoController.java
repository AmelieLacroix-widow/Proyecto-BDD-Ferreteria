package com.miempresa.ferreteria.controller;

import com.miempresa.ferreteria.model.Producto;
import com.miempresa.ferreteria.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    // GET /productos
    @GetMapping
    public List<Producto> listar() {
        return service.todos();
    }

    // GET /productos/{codigoBarras}
    // La PK de Producto es String, no Integer
    @GetMapping("/{codigoBarras}")
    public ResponseEntity<Producto> obtener(@PathVariable String codigoBarras) {
        return service.obtener(codigoBarras)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /productos/buscar?texto=...
    @GetMapping("/buscar")
    public List<Producto> buscar(@RequestParam String texto) {
        return service.buscar(texto);
    }

    // GET /productos/departamento/{idDepartamento}
    @GetMapping("/departamento/{idDepartamento}")
    public List<Producto> porDepartamento(@PathVariable Integer idDepartamento) {
        return service.porDepartamento(idDepartamento);
    }

    // GET /productos/proveedor/{idProveedor}
    @GetMapping("/proveedor/{idProveedor}")
    public List<Producto> porProveedor(@PathVariable Integer idProveedor) {
        return service.porProveedor(idProveedor);
    }

    // GET /productos/inventario
    @GetMapping("/inventario")
    public List<Producto> inventarioActivo() {
        return service.inventarioActivo();
    }

    // GET /productos/inventario/bajo-minimo
    @GetMapping("/inventario/bajo-minimo")
    public List<Producto> bajoMinimo() {
        return service.bajoMinimo();
    }

    // POST /productos
    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(producto));
    }

    // PUT /productos/{codigoBarras}
    @PutMapping("/{codigoBarras}")
    public ResponseEntity<Producto> actualizar(@PathVariable String codigoBarras,
                                               @RequestBody Producto producto) {
        return service.obtener(codigoBarras).map(existing -> {
            producto.setCodigoBarras(codigoBarras);
            return ResponseEntity.ok(service.guardar(producto));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /productos/{codigoBarras}
    // Retorna 409 Conflict si el producto tiene renglones en tickets (ON DELETE RESTRICT)
    @DeleteMapping("/{codigoBarras}")
    public ResponseEntity<?> eliminar(@PathVariable String codigoBarras) {
        if (service.obtener(codigoBarras).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        boolean eliminado = service.eliminar(codigoBarras);
        if (!eliminado) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar: el producto tiene ventas registradas.");
        }
        return ResponseEntity.noContent().build();
    }
}
