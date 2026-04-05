package com.miempresa.ferreteria.controller;

import com.miempresa.ferreteria.model.Ticket;
import com.miempresa.ferreteria.service.TicketService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    // GET /tickets
    @GetMapping
    public List<Ticket> listar() {
        return service.todos();
    }

    // GET /tickets/{folio}
    @GetMapping("/{folio}")
    public ResponseEntity<Ticket> obtener(@PathVariable Integer folio) {
        return service.buscarPorId(folio)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /tickets/activos/{idUsuario}
    // Tickets abiertos del cajero en turno
    @GetMapping("/activos/{idUsuario}")
    public List<Ticket> activos(@PathVariable Integer idUsuario) {
        return service.activos(idUsuario);
    }

    // GET /tickets/fecha?fecha=2025-06-01
    @GetMapping("/fecha")
    public List<Ticket> porFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return service.porFecha(fecha);
    }

    // GET /tickets/rango?desde=2025-06-01&hasta=2025-06-30
    @GetMapping("/rango")
    public List<Ticket> rango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return service.rango(desde, hasta);
    }

    // GET /tickets/cliente/{idCliente}
    @GetMapping("/cliente/{idCliente}")
    public List<Ticket> porCliente(@PathVariable Integer idCliente) {
        return service.porCliente(idCliente);
    }

    // GET /tickets/{folio}/derivados
    // Re-Tickets y Devoluciones que apuntan a este folio
    @GetMapping("/{folio}/derivados")
    public List<Ticket> derivados(@PathVariable Integer folio) {
        return service.derivados(folio);
    }

    // POST /tickets
    @PostMapping
    public ResponseEntity<Ticket> crear(@RequestBody Ticket ticket) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(ticket));
    }

    // PUT /tickets/{folio}
    @PutMapping("/{folio}")
    public ResponseEntity<Ticket> actualizar(@PathVariable Integer folio,
                                             @RequestBody Ticket ticket) {
        return service.buscarPorId(folio).map(existing -> {
            ticket.setFolioTicket(folio);
            return ResponseEntity.ok(service.guardar(ticket));
        }).orElse(ResponseEntity.notFound().build());
    }

    // PATCH /tickets/{folio}/cancelar
    // Cancelar un ticket sin eliminarlo (cambia estado a "Cancelado")
    @PatchMapping("/{folio}/cancelar")
    public ResponseEntity<Ticket> cancelar(@PathVariable Integer folio) {
        return service.cancelar(folio)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
