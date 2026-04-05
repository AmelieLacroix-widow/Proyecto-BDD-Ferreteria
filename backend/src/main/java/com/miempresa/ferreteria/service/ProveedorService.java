package com.miempresa.ferreteria.service;

import com.miempresa.ferreteria.model.Proveedor;
import com.miempresa.ferreteria.repository.ProveedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorService {

    private final ProveedorRepository repo;

    public ProveedorService(ProveedorRepository repo) {
        this.repo = repo;
    }

    public List<Proveedor> todos() {
        return repo.findAll();
    }

    public Optional<Proveedor> buscarPorId(Integer id) {
        return repo.findById(id);
    }

    public List<Proveedor> buscar(String nombre) {
        return repo.findByNombreProveedorContainingIgnoreCase(nombre);
    }

    public Proveedor guardar(Proveedor p) {
        return repo.save(p);
    }

    /**
     * Elimina un proveedor por ID.
     * Si hay productos con este proveedor asignado, la BD pondrá null
     * en su id_proveedor (ON DELETE SET NULL en PRODUCTO).
     */
    public void eliminar(Integer id) {
        repo.deleteById(id);
    }
}
