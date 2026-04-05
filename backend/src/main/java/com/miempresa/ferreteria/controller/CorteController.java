package com.miempresa.ferreteria.controller;

import com.miempresa.ferreteria.model.Corte;
import com.miempresa.ferreteria.service.CorteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/cortes")
public class CorteController {

    private final CorteService service;

    public CorteController(CorteService service) {
        this.service = service;
    }

    // GET /cortes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Corte> obtener(@PathVariable Integer id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /cortes/ultimo
    // Devuelve el corte más reciente registrado
    @GetMapping("/ultimo")
    public ResponseEntity<Corte> ultimo() {
        return service.ultimoCorte()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /cortes/fecha?fecha=2025-06-01
    @GetMapping("/fecha")
    public ResponseEntity<Corte> porFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return service.buscarPorFecha(fecha)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /cortes/rango?desde=2025-06-01&hasta=2025-06-30
    @GetMapping("/rango")
    public List<Corte> rango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return service.rango(desde, hasta);
    }

    // GET /cortes/precalcular?fecha=2025-06-01
    // Devuelve un Corte con los totales del día calculados desde BD,
    // sin guardarlo — el cajero completa billetes y fondo y llama a POST /cortes
    @GetMapping("/precalcular")
    public ResponseEntity<?> precalcular(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        if (service.existeCorte(fecha)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe un corte registrado para esa fecha.");
        }
        return ResponseEntity.ok(service.precalcular(fecha));
    }

    // POST /cortes
    // Guarda el corte final con billetes y fondo capturados por el cajero
    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Corte corte) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(corte));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
