package com.miempresa.ferreteria.service;

import com.miempresa.ferreteria.model.Pago;
import com.miempresa.ferreteria.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PagoService {

    @Autowired
    private PagoRepository repo;

    public boolean existePago(Integer folio) {
        return repo.existsByTicket_FolioTicket(folio);
    }

    public Pago guardar(Pago p) {
        return repo.save(p);
    }

    public java.math.BigDecimal totalEfectivo(LocalDate fecha) {
        return repo.sumEfectivoByFecha(fecha);
    }
}