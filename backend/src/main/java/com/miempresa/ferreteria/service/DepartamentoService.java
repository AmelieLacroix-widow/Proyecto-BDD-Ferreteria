package com.miempresa.ferreteria.service;

import com.miempresa.ferreteria.model.Departamento;
import com.miempresa.ferreteria.repository.DepartamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartamentoService {

    private final DepartamentoRepository repo;

    public DepartamentoService(DepartamentoRepository repo) {
        this.repo = repo;
    }

    public List<Departamento> todos() {
        return repo.findAll();
    }

    public Optional<Departamento> buscarPorId(Integer id) {
        return repo.findById(id);
    }

    public Optional<Departamento> buscarNombre(String nombre) {
        return repo.findByNombreDepartamento(nombre);
    }

    public boolean existe(String nombre) {
        return repo.existsByNombreDepartamento(nombre);
    }

    public Departamento guardar(Departamento d) {
        return repo.save(d);
    }

    /**
     * Elimina un departamento por ID.
     * Si hay productos con este departamento asignado, la BD pondrá null
     * en su id_departamento (ON DELETE SET NULL en PRODUCTO).
     */
    public void eliminar(Integer id) {
        repo.deleteById(id);
    }
}
