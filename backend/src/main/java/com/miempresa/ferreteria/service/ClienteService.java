package com.miempresa.ferreteria.service;

import com.miempresa.ferreteria.model.Cliente;
import com.miempresa.ferreteria.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository repo;

    public ClienteService(ClienteRepository repo) {
        this.repo = repo;
    }

    public List<Cliente> todos() {
        return repo.findAll();
    }

    public Optional<Cliente> buscarPorId(Integer id) {
        return repo.findById(id);
    }

    public List<Cliente> buscarNombre(String nombre) {
        return repo.findByNombresContainingIgnoreCase(nombre);
    }

    public List<Cliente> buscarApellido(String apellido) {
        return repo.findByApellidoPaternoContainingIgnoreCase(apellido);
    }

    public List<Cliente> buscarTelefono(String telefono) {
        return repo.findByTelefono(telefono);
    }

    public List<Cliente> conCredito() {
        return repo.findByTieneCreditoTrue();
    }

    public List<Cliente> sinCredito() {
        return repo.findByTieneCreditoFalse();
    }

    public Cliente guardar(Cliente c) {
        return repo.save(c);
    }

    /**
     * Elimina un cliente por ID.
     * Nota: si el cliente tiene tickets asociados, la BD lanzará error
     * porque TICKET.id_cliente tiene ON DELETE SET NULL — en ese caso
     * JPA simplemente pondrá null en los tickets antes de eliminar.
     */
    public void eliminar(Integer id) {
        repo.deleteById(id);
    }
}
