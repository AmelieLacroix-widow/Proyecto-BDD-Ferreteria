package com.miempresa.ferreteria.service;

import com.miempresa.ferreteria.model.Pago;
import com.miempresa.ferreteria.repository.PagoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class PagoService {

    private final PagoRepository repo;

    public PagoService(PagoRepository repo) {
        this.repo = repo;
    }

    public Optional<Pago> buscarPorTicket(Integer folioTicket) {
        return repo.findByTicket_FolioTicket(folioTicket);
    }

    public boolean existePago(Integer folioTicket) {
        return repo.existsByTicket_FolioTicket(folioTicket);
    }

    public Pago guardar(Pago p) {
        return repo.save(p);
    }

    // --- Métodos de totales del día usados por CorteService ---

    public BigDecimal totalEfectivo(LocalDate fecha) {
        return repo.sumEfectivoByFecha(fecha);
    }

    public BigDecimal totalTarjeta(LocalDate fecha) {
        return repo.sumTarjetaByFecha(fecha);
    }

    public BigDecimal totalTransferencia(LocalDate fecha) {
        return repo.sumTransferenciaByFecha(fecha);
    }

    public BigDecimal totalCredito(LocalDate fecha) {
        return repo.sumCreditoByFecha(fecha);
    }

    public BigDecimal totalCheque(LocalDate fecha) {
        return repo.sumChequeByFecha(fecha);
    }
}
