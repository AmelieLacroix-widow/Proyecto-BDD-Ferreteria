package com.miempresa.ferreteria.controller;

import com.miempresa.ferreteria.model.Pago;
import com.miempresa.ferreteria.model.Ticket;
import com.miempresa.ferreteria.service.PagoService;
import com.miempresa.ferreteria.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets/{folio}/pago")
public class PagoController {

    private final PagoService service;
    private final TicketService ticketService;

    public PagoController(PagoService service, TicketService ticketService) {
        this.service = service;
        this.ticketService = ticketService;
    }

    // GET /tickets/{folio}/pago
    // Obtiene el pago registrado para el ticket
    @GetMapping
    public ResponseEntity<Pago> obtener(@PathVariable Integer folio) {
        return service.buscarPorTicket(folio)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /tickets/{folio}/pago
    // Registra el pago de un ticket (solo si no tiene pago previo)
    @PostMapping
    public ResponseEntity<?> crear(@PathVariable Integer folio, @RequestBody Pago pago) {
        if (ticketService.buscarPorId(folio).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (service.existePago(folio)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Este ticket ya tiene un pago registrado.");
        }
        // Asociar el ticket al pago y marcar el ticket como Pagado
        Ticket ticket = ticketService.buscarPorId(folio).get();
        pago.setTicket(ticket);
        ticket.setEstadoDocumento("Pagado");
        ticketService.guardar(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(pago));
    }
}
