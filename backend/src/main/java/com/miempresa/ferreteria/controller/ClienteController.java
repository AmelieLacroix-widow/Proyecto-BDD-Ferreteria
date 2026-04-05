package com.miempresa.ferreteria.controller;

import com.miempresa.ferreteria.model.Cliente;
import com.miempresa.ferreteria.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService service;

    @GetMapping("/nombre")
    public List<Cliente> buscarNombre(@RequestParam String nombre) {
        return service.buscarNombre(nombre);
    }

    @GetMapping("/apellido")
    public List<Cliente> buscarApellido(@RequestParam String apellido) {
        return service.buscarApellido(apellido);
    }

    @GetMapping("/credito")
    public List<Cliente> conCredito() {
        return service.conCredito();
    }

    @GetMapping("/sin-credito")
    public List<Cliente> sinCredito() {
        return service.sinCredito();
    }

    @GetMapping("/telefono")
    public List<Cliente> buscarTelefono(@RequestParam String telefono) {
        return service.buscarTelefono(telefono);
    }

    @GetMapping("/todos")
    public List<Cliente> todos() {
        return service.todos();
    }

    // 🔥 BUSCAR POR ID (PRO)
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        Cliente cliente = service.buscarPorId(id);

        if (cliente == null) {
            return ResponseEntity.status(404).body("Cliente no encontrado");
        }

        return ResponseEntity.ok(cliente);
    }

    // 🔥 CREAR
    @PostMapping("/guardar")
    public Cliente guardar(@RequestBody Cliente cliente) {
        return service.guardar(cliente);
    }

    // 🔥 ACTUALIZAR
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Cliente cliente) {
        Cliente actualizado = service.actualizar(id, cliente);

        if (actualizado == null) {
            return ResponseEntity.status(404).body("Cliente no encontrado");
        }

        return ResponseEntity.ok(actualizado);
    }

    // 🔥 ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        Cliente cliente = service.buscarPorId(id);

        if (cliente == null) {
            return ResponseEntity.status(404).body("Cliente no encontrado");
        }

        service.eliminar(id);
        return ResponseEntity.ok("Cliente eliminado correctamente");
    }
}