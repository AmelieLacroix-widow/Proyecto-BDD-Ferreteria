package com.miempresa.ferreteria.controller;

import com.miempresa.ferreteria.model.Cliente;
import com.miempresa.ferreteria.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    // GET /clientes
    @GetMapping
    public List<Cliente> listar() {
        return service.todos();
    }

    // GET /clientes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtener(@PathVariable Integer id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /clientes/buscar?nombre=...
    @GetMapping("/buscar")
    public List<Cliente> buscarNombre(@RequestParam String nombre) {
        return service.buscarNombre(nombre);
    }

    // GET /clientes/buscar-apellido?apellido=...
    @GetMapping("/buscar-apellido")
    public List<Cliente> buscarApellido(@RequestParam String apellido) {
        return service.buscarApellido(apellido);
    }

    // GET /clientes/buscar-telefono?telefono=...
    @GetMapping("/buscar-telefono")
    public List<Cliente> buscarTelefono(@RequestParam String telefono) {
        return service.buscarTelefono(telefono);
    }

    // GET /clientes/con-credito
    @GetMapping("/con-credito")
    public List<Cliente> conCredito() {
        return service.conCredito();
    }

    // GET /clientes/sin-credito
    @GetMapping("/sin-credito")
    public List<Cliente> sinCredito() {
        return service.sinCredito();
    }

    // POST /clientes
    @PostMapping
    public ResponseEntity<Cliente> crear(@RequestBody Cliente cliente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(cliente));
    }

    // PUT /clientes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(@PathVariable Integer id,
                                              @RequestBody Cliente cliente) {
        return service.buscarPorId(id).map(existing -> {
            cliente.setIdCliente(id);
            return ResponseEntity.ok(service.guardar(cliente));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /clientes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (service.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
