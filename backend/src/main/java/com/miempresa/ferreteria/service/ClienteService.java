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

    // 🔥 ACTUALIZAR
    public Cliente actualizar(Integer id, Cliente cliente) {

        Cliente existente = repo.findById(id).orElse(null);

        if (existente == null) {
            return null;
        }

        existente.setNombres(cliente.getNombres());
        existente.setApellidoPaterno(cliente.getApellidoPaterno());
        existente.setApellidoMaterno(cliente.getApellidoMaterno());
        existente.setTelefono(cliente.getTelefono());
        existente.setCorreo(cliente.getCorreo());
        existente.setDomicilio(cliente.getDomicilio());
        existente.setColonia(cliente.getColonia());
        existente.setMunicipioEstado(cliente.getMunicipioEstado());
        existente.setCodigoPostal(cliente.getCodigoPostal());
        existente.setNotas(cliente.getNotas());
        existente.setTieneCredito(cliente.getTieneCredito());
        existente.setSaldoCredito(cliente.getSaldoCredito());
        existente.setLimiteCredito(cliente.getLimiteCredito());

        return repo.save(existente);
    }

    // 🔥 ELIMINAR
    public void eliminar(Integer id) {
        repo.deleteById(id);
    }

    // 🔥 BUSCAR POR ID
    public Cliente buscarPorId(Integer id) {
        return repo.findById(id).orElse(null);
    }
}