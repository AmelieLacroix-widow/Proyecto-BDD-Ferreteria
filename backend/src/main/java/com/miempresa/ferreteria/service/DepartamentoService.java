package com.miempresa.ferreteria.service;

import com.miempresa.ferreteria.model.Departamento;
import com.miempresa.ferreteria.repository.DepartamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DepartamentoService {

    @Autowired
    private DepartamentoRepository repo;

    public Optional<Departamento> buscarNombre(String nombre) {
        return repo.findByNombreDepartamento(nombre);
    }

    public boolean existe(String nombre) {
        return repo.existsByNombreDepartamento(nombre);
    }

    public Departamento guardar(Departamento d) {
        return repo.save(d);
    }
}