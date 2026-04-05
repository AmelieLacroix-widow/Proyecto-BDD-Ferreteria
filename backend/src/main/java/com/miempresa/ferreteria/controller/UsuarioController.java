package com.miempresa.ferreteria.controller;

import com.miempresa.ferreteria.model.Usuario;
import com.miempresa.ferreteria.service.UsuarioService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    // GET /usuarios
    @GetMapping
    public List<Usuario> listar() {
        return service.todos();
    }

    // GET /usuarios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtener(@PathVariable Integer id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /usuarios
    // Valida nombre de usuario único antes de crear
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Usuario usuario) {
        if (service.existe(usuario.getNombreUsuario())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El nombre de usuario ya está en uso.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario));
    }

    // PUT /usuarios/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id,
                                        @RequestBody Usuario usuario) {
        return service.buscarPorId(id).map(existing -> {
            // Si cambia el nombre de usuario, verificar que no esté tomado
            if (!existing.getNombreUsuario().equals(usuario.getNombreUsuario())
                    && service.existe(usuario.getNombreUsuario())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("El nombre de usuario ya está en uso.");
            }
            usuario.setIdUsuario(id);
            return ResponseEntity.ok(service.guardar(usuario));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /usuarios/{id}
    // Retorna 409 si el usuario tiene tickets o retiros (ON DELETE RESTRICT)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        if (service.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar: el usuario tiene transacciones registradas.");
        }
    }
}
