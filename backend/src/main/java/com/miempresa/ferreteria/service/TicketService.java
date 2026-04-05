package com.miempresa.ferreteria.service;

import com.miempresa.ferreteria.model.Ticket;
import com.miempresa.ferreteria.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository repo;

    public TicketService(TicketRepository repo) {
        this.repo = repo;
    }

    public List<Ticket> todos() {
        return repo.findAll();
    }

    public Optional<Ticket> buscarPorId(Integer folioTicket) {
        return repo.findById(folioTicket);
    }

    /** Tickets con estado "Activo" del cajero en turno. */
    public List<Ticket> activos(Integer idUsuario) {
        return repo.findByUsuario_IdUsuarioAndEstadoDocumento(idUsuario, "Activo");
    }

    public List<Ticket> porFecha(LocalDate fecha) {
        return repo.findByFechaTransaccion(fecha);
    }

    public List<Ticket> porFechaYTipo(LocalDate fecha, String estadoDocumento, String tipoDocumento) {
        return repo.findByFechaTransaccionAndEstadoDocumentoAndTipoDocumento(fecha, estadoDocumento, tipoDocumento);
    }

    public List<Ticket> porCliente(Integer idCliente) {
        return repo.findByCliente_IdCliente(idCliente);
    }

    public List<Ticket> porClienteYTipo(Integer idCliente, String tipoDocumento) {
        return repo.findByCliente_IdClienteAndTipoDocumento(idCliente, tipoDocumento);
    }

    public List<Ticket> rango(LocalDate desde, LocalDate hasta) {
        return repo.findByFechaTransaccionBetween(desde, hasta);
    }

    /** Devuelve los Re-Tickets o Devoluciones que derivan de un ticket original. */
    public List<Ticket> derivados(Integer folioTicketOriginal) {
        return repo.findByTicketReferencia_FolioTicket(folioTicketOriginal);
    }

    /**
     * Suma totalNeto de tickets Pagados de tipo "Ticket" en una fecha.
     * Llamado por CorteService para calcular totalVentasDia.
     */
    public BigDecimal totalVentasDia(LocalDate fecha) {
        return repo.sumTotalNetoPagadoByFecha(fecha);
    }

    public Ticket guardar(Ticket t) {
        return repo.save(t);
    }

    /**
     * Marca un ticket como Cancelado.
     * El controller debe llamar también a DetalleTicketService.eliminarPorTicket()
     * si se requiere limpiar el detalle antes de cancelar.
     */
    public Optional<Ticket> cancelar(Integer folioTicket) {
        return repo.findById(folioTicket).map(t -> {
            t.setEstadoDocumento("Cancelado");
            return repo.save(t);
        });
    }
}
