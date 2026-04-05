package com.miempresa.ferreteria.service;

import com.miempresa.ferreteria.model.Retiro;
import com.miempresa.ferreteria.repository.RetiroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RetiroService {

    @Autowired
    private RetiroRepository repo;

    public List<Retiro> porFecha(LocalDate fecha) {
        return repo.findByFechaRetiro(fecha);
    }

    public List<Retiro> rango(LocalDate desde, LocalDate hasta) {
        return repo.findByFechaRetiroBetween(desde, hasta);
    }

    public Retiro guardar(Retiro r) {
        return repo.save(r);
    }
}