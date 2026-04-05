package com.miempresa.ferreteria.service;

import com.miempresa.ferreteria.model.Usuario;
import com.miempresa.ferreteria.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;

    public UsuarioService(UsuarioRepository repo) {
        this.repo = repo;
    }

    public List<Usuario> todos() {
        return repo.findAll();
    }

    public Optional<Usuario> buscarPorId(Integer id) {
        return repo.findById(id);
    }

    public Optional<Usuario> buscar(String nombreUsuario) {
        return repo.findByNombreUsuario(nombreUsuario);
    }

    public boolean existe(String nombreUsuario) {
        return repo.existsByNombreUsuario(nombreUsuario);
    }

    public Usuario guardar(Usuario u) {
        return repo.save(u);
    }

    /**
     * Elimina un usuario por ID.
     * La FK en TICKET y RETIRO es ON DELETE RESTRICT, así que si el usuario
     * tiene transacciones asociadas la BD lanzará error. El controller
     * debe capturar DataIntegrityViolationException y devolver un mensaje apropiado.
     */
    public void eliminar(Integer id) {
        repo.deleteById(id);
    }
}
