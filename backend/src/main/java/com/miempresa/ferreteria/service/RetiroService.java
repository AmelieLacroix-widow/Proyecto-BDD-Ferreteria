package com.miempresa.ferreteria.service;

import com.miempresa.ferreteria.model.Retiro;
import com.miempresa.ferreteria.repository.RetiroRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RetiroService {

    private final RetiroRepository repo;

    public RetiroService(RetiroRepository repo) {
        this.repo = repo;
    }

    public Optional<Retiro> buscarPorId(Integer id) {
        return repo.findById(id);
    }

    public List<Retiro> porFecha(LocalDate fecha) {
        return repo.findByFechaRetiro(fecha);
    }

    public List<Retiro> rango(LocalDate desde, LocalDate hasta) {
        return repo.findByFechaRetiroBetween(desde, hasta);
    }

    public List<Retiro> porUsuario(Integer idUsuario) {
        return repo.findByUsuario_IdUsuario(idUsuario);
    }

    public List<Retiro> porUsuarioYFecha(Integer idUsuario, LocalDate fecha) {
        return repo.findByUsuario_IdUsuarioAndFechaRetiro(idUsuario, fecha);
    }

    /**
     * Suma total de retiros de una fecha.
     * Llamado por CorteService para calcular totalGastosDia.
     */
    public BigDecimal totalGastosDia(LocalDate fecha) {
        return repo.sumMontoByFecha(fecha);
    }

    public Retiro guardar(Retiro r) {
        return repo.save(r);
    }

    public void eliminar(Integer id) {
        repo.deleteById(id);
    }
}
