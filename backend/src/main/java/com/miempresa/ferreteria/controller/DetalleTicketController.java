package com.miempresa.ferreteria.controller;

import com.miempresa.ferreteria.model.DetalleTicket;
import com.miempresa.ferreteria.service.DetalleTicketService;
import com.miempresa.ferreteria.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets/{folio}/detalle")
public class DetalleTicketController {

    private final DetalleTicketService service;
    private final TicketService ticketService;

    public DetalleTicketController(DetalleTicketService service, TicketService ticketService) {
        this.service = service;
        this.ticketService = ticketService;
    }

    // GET /tickets/{folio}/detalle
    // Todos los renglones del ticket
    @GetMapping
    public ResponseEntity<List<DetalleTicket>> listar(@PathVariable Integer folio) {
        if (ticketService.buscarPorId(folio).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(service.porTicket(folio));
    }

    // GET /tickets/{folio}/detalle/{codigoBarras}
    // Un renglón específico
    @GetMapping("/{codigoBarras}")
    public ResponseEntity<DetalleTicket> obtener(@PathVariable Integer folio,
                                                 @PathVariable String codigoBarras) {
        return service.buscar(folio, codigoBarras)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /tickets/{folio}/detalle
    // Agrega un renglón al ticket
    @PostMapping
    public ResponseEntity<DetalleTicket> agregar(@PathVariable Integer folio,
                                                 @RequestBody DetalleTicket detalle) {
        if (ticketService.buscarPorId(folio).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        detalle.setFolioTicket(folio);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(detalle));
    }

    // PUT /tickets/{folio}/detalle/{codigoBarras}
    // Actualiza cantidad o precio de un renglón
    @PutMapping("/{codigoBarras}")
    public ResponseEntity<DetalleTicket> actualizar(@PathVariable Integer folio,
                                                    @PathVariable String codigoBarras,
                                                    @RequestBody DetalleTicket detalle) {
        return service.buscar(folio, codigoBarras).map(existing -> {
            detalle.setFolioTicket(folio);
            detalle.setCodigoBarras(codigoBarras);
            return ResponseEntity.ok(service.guardar(detalle));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /tickets/{folio}/detalle/{codigoBarras}
    // Elimina un renglón específico
    @DeleteMapping("/{codigoBarras}")
    public ResponseEntity<Void> eliminarRenglon(@PathVariable Integer folio,
                                                @PathVariable String codigoBarras) {
        if (service.buscar(folio, codigoBarras).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.eliminarRenglon(folio, codigoBarras);
        return ResponseEntity.noContent().build();
    }

    // DELETE /tickets/{folio}/detalle
    // Elimina todos los renglones del ticket (usado al cancelar)
    @DeleteMapping
    public ResponseEntity<Void> eliminarTodos(@PathVariable Integer folio) {
        if (ticketService.buscarPorId(folio).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.eliminarPorTicket(folio);
        return ResponseEntity.noContent().build();
    }
}
