package com.miempresa.ferreteria.controller;

import com.miempresa.ferreteria.model.Modulo;
import com.miempresa.ferreteria.service.ModuloService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/modulos")
public class ModuloController {

    private final ModuloService service;

    public ModuloController(ModuloService service) {
        this.service = service;
    }

    // GET /modulos
    @GetMapping
    public List<Modulo> listar() {
        return service.todos();
    }

    // GET /modulos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Modulo> obtener(@PathVariable Integer id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
