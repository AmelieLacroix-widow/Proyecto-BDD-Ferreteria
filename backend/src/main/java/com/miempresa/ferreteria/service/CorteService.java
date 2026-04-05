package com.miempresa.ferreteria.service;

import com.miempresa.ferreteria.model.Corte;
import com.miempresa.ferreteria.repository.CorteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CorteService {

    private final CorteRepository repo;
    private final TicketService ticketService;
    private final PagoService pagoService;
    private final RetiroService retiroService;

    public CorteService(CorteRepository repo,
                        TicketService ticketService,
                        PagoService pagoService,
                        RetiroService retiroService) {
        this.repo = repo;
        this.ticketService = ticketService;
        this.pagoService = pagoService;
        this.retiroService = retiroService;
    }

    public Optional<Corte> buscarPorId(Integer id) {
        return repo.findById(id);
    }

    public Optional<Corte> buscarPorFecha(LocalDate fecha) {
        return repo.findFirstByFechaCorte(fecha);
    }

    public Optional<Corte> ultimoCorte() {
        return repo.findTopByOrderByFechaCorteDesc();
    }

    public boolean existeCorte(LocalDate fecha) {
        return repo.existsByFechaCorte(fecha);
    }

    public List<Corte> rango(LocalDate desde, LocalDate hasta) {
        return repo.findByFechaCorteBetweenOrderByFechaCorteDesc(desde, hasta);
    }

    public List<Corte> porUsuario(Integer idUsuario) {
        return repo.findByUsuario_IdUsuario(idUsuario);
    }

    /**
     * Precalcula los totales del día a partir de los tickets y retiros registrados.
     * El controller recibe este objeto, el cajero completa el conteo de billetes
     * y el fondo entregado, y luego llama a guardar().
     *
     * @param fecha  Fecha del día a cortar (normalmente LocalDate.now())
     * @return Corte con los campos de ventas y gastos ya calculados desde BD.
     *         Los campos de billetes, fondo inicial/final y total_entregado
     *         quedan en cero para que el cajero los capture manualmente.
     */
    public Corte precalcular(LocalDate fecha) {
        Corte c = new Corte();
        c.setFechaCorte(fecha);

        // Totales de ventas del día (solo tickets Pagados de tipo Ticket)
        c.setTotalVentasDia(ticketService.totalVentasDia(fecha));

        // Totales por método de pago
        c.setTotalEfectivoDia(pagoService.totalEfectivo(fecha));
        c.setTotalTarjetaDia(pagoService.totalTarjeta(fecha));
        c.setTotalTransferenciaDia(pagoService.totalTransferencia(fecha));
        c.setTotalCreditoDia(pagoService.totalCredito(fecha));
        c.setTotalChequeDia(pagoService.totalCheque(fecha));

        // Gastos y retiros del día
        c.setTotalGastosDia(retiroService.totalGastosDia(fecha));

        return c;
    }

    /**
     * Guarda el corte final con todos los campos ya capturados por el cajero.
     * Lanza IllegalStateException si ya existe un corte para esa fecha.
     */
    public Corte guardar(Corte corte) {
        if (repo.existsByFechaCorte(corte.getFechaCorte())) {
            throw new IllegalStateException(
                "Ya existe un corte registrado para la fecha: " + corte.getFechaCorte());
        }
        return repo.save(corte);
    }
}
