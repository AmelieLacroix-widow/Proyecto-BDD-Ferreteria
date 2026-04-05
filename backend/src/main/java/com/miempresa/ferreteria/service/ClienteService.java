package com.miempresa.ferreteria.service;

import com.miempresa.ferreteria.model.Cliente;
import com.miempresa.ferreteria.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repo;

    public List<Cliente> buscarNombre(String nombre) {
        return repo.findByNombresContainingIgnoreCase(nombre);
    }

    public List<Cliente> buscarApellido(String apellido) {
        return repo.findByApellidoPaternoContainingIgnoreCase(apellido);
    }

    public List<Cliente> conCredito() {
        return repo.findByTieneCreditoTrue();
    }

    public List<Cliente> sinCredito() {
        return repo.findByTieneCreditoFalse();
    }

    public List<Cliente> buscarTelefono(String telefono) {
        return repo.findByTelefono(telefono);
    }

    public List<Cliente> todos() {
        return repo.findAll();
    }

    public Cliente guardar(Cliente c) {
        return repo.save(c);
    }
}