package com.miempresa.ferreteria.service;

import com.miempresa.ferreteria.model.DetalleTicket;
import com.miempresa.ferreteria.model.DetalleTicketId;
import com.miempresa.ferreteria.repository.DetalleTicketRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DetalleTicketService {

    private final DetalleTicketRepository repo;

    public DetalleTicketService(DetalleTicketRepository repo) {
        this.repo = repo;
    }

    /** Devuelve todos los renglones de un ticket (la tabla de productos en pantalla). */
    public List<DetalleTicket> porTicket(Integer folioTicket) {
        return repo.findByFolioTicket(folioTicket);
    }

    /** Busca un renglón específico dentro de un ticket. */
    public Optional<DetalleTicket> buscar(Integer folioTicket, String codigoBarras) {
        return repo.findByFolioTicketAndCodigoBarras(folioTicket, codigoBarras);
    }

    /** Cuenta cuántos productos distintos tiene un ticket. */
    public long contarRenglones(Integer folioTicket) {
        return repo.countByFolioTicket(folioTicket);
    }

    public DetalleTicket guardar(DetalleTicket d) {
        return repo.save(d);
    }

    /**
     * Elimina un renglón específico del ticket (botón "Eliminar producto" en pantalla).
     * El controller debe recalcular los totales del Ticket después de llamar esto.
     */
    public void eliminarRenglon(Integer folioTicket, String codigoBarras) {
        repo.deleteByFolioTicketAndCodigoBarras(folioTicket, codigoBarras);
    }

    /**
     * Elimina todos los renglones de un ticket.
     * Llamado por el controller al cancelar un ticket antes de cambiar su estado.
     */
    public void eliminarPorTicket(Integer folioTicket) {
        repo.deleteByFolioTicket(folioTicket);
    }

    /**
     * Verifica si un producto tiene historial de ventas.
     * Usado por ProductoService.eliminar() para respetar el ON DELETE RESTRICT.
     */
    public boolean productoTieneVentas(String codigoBarras) {
        return repo.existsByCodigoBarras(codigoBarras);
    }

    /**
     * Suma la cantidad vendida de un producto en un rango de fechas.
     * Usado en reportes de productos más vendidos.
     */
    public BigDecimal cantidadVendida(String codigoBarras, LocalDate desde, LocalDate hasta) {
        return repo.sumCantidadVendidaByProductoAndFechas(codigoBarras, desde, hasta);
    }
}
