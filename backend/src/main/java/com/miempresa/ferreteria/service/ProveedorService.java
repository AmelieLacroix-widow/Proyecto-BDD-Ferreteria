package com.miempresa.ferreteria.service;

import com.miempresa.ferreteria.model.Proveedor;
import com.miempresa.ferreteria.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository repo;

    public List<Proveedor> buscar(String nombre) {
        return repo.findByNombreProveedorContainingIgnoreCase(nombre);
    }

    public List<Proveedor> todos() {
        return repo.findAll();
    }

    public Proveedor guardar(Proveedor p) {
        return repo.save(p);
    }
}