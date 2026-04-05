package com.miempresa.ferreteria.controller;

import com.miempresa.ferreteria.model.UsuarioModulo;
import com.miempresa.ferreteria.service.UsuarioModuloService;
import com.miempresa.ferreteria.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios/{idUsuario}/modulos")
public class UsuarioModuloController {

    private final UsuarioModuloService service;
    private final UsuarioService usuarioService;

    public UsuarioModuloController(UsuarioModuloService service, UsuarioService usuarioService) {
        this.service = service;
        this.usuarioService = usuarioService;
    }

    // GET /usuarios/{idUsuario}/modulos
    // Devuelve los módulos asignados a un usuario
    @GetMapping
    public ResponseEntity<?> listar(@PathVariable Integer idUsuario) {
        if (usuarioService.buscarPorId(idUsuario).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(service.nombresModulos(idUsuario));
    }

    // POST /usuarios/{idUsuario}/modulos/{idModulo}
    // Asigna un módulo al usuario
    @PostMapping("/{idModulo}")
    public ResponseEntity<?> asignar(@PathVariable Integer idUsuario,
                                     @PathVariable Integer idModulo) {
        if (usuarioService.buscarPorId(idUsuario).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (service.tieneAcceso(idUsuario, idModulo)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El usuario ya tiene acceso a ese módulo.");
        }
        UsuarioModulo um = service.asignar(idUsuario, idModulo);
        return ResponseEntity.status(HttpStatus.CREATED).body(um);
    }

    // DELETE /usuarios/{idUsuario}/modulos/{idModulo}
    // Revoca un módulo específico del usuario
    @DeleteMapping("/{idModulo}")
    public ResponseEntity<Void> revocar(@PathVariable Integer idUsuario,
                                        @PathVariable Integer idModulo) {
        if (!service.tieneAcceso(idUsuario, idModulo)) {
            return ResponseEntity.notFound().build();
        }
        service.revocar(idUsuario, idModulo);
        return ResponseEntity.noContent().build();
    }

    // DELETE /usuarios/{idUsuario}/modulos
    // Revoca todos los módulos del usuario
    @DeleteMapping
    public ResponseEntity<Void> revocarTodos(@PathVariable Integer idUsuario) {
        if (usuarioService.buscarPorId(idUsuario).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.revocarTodos(idUsuario);
        return ResponseEntity.noContent().build();
    }
}
