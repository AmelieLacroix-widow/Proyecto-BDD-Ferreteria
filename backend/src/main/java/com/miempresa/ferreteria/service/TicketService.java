package com.miempresa.ferreteria.service;

import com.miempresa.ferreteria.model.Ticket;
import com.miempresa.ferreteria.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketRepository repo;

    public List<Ticket> activos(Integer idUsuario) {
        return repo.findByUsuario_IdUsuarioAndEstadoDocumento(idUsuario, "Activo");
    }

    public List<Ticket> porFecha(LocalDate fecha) {
        return repo.findByFechaTransaccion(fecha);
    }

    public Ticket guardar(Ticket t) {
        return repo.save(t);
    }

    public List<Ticket> todos() {
        return repo.findAll();
    }
}