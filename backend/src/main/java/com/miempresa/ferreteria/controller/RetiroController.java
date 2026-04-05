package com.miempresa.ferreteria.controller;

import com.miempresa.ferreteria.model.Retiro;
import com.miempresa.ferreteria.service.RetiroService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/retiros")
public class RetiroController {

    private final RetiroService service;

    public RetiroController(RetiroService service) {
        this.service = service;
    }

    // GET /retiros/fecha?fecha=2025-06-01
    @GetMapping("/fecha")
    public List<Retiro> porFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return service.porFecha(fecha);
    }

    // GET /retiros/rango?desde=2025-06-01&hasta=2025-06-30
    @GetMapping("/rango")
    public List<Retiro> rango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return service.rango(desde, hasta);
    }

    // GET /retiros/usuario/{idUsuario}
    @GetMapping("/usuario/{idUsuario}")
    public List<Retiro> porUsuario(@PathVariable Integer idUsuario) {
        return service.porUsuario(idUsuario);
    }

    // GET /retiros/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Retiro> obtener(@PathVariable Integer id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /retiros
    @PostMapping
    public ResponseEntity<Retiro> crear(@RequestBody Retiro retiro) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(retiro));
    }

    // DELETE /retiros/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (service.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
