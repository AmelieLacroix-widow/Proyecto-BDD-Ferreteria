package com.miempresa.ferreteria.service;

import com.miempresa.ferreteria.model.Usuario;
import com.miempresa.ferreteria.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repo;

    public Optional<Usuario> buscar(String nombre) {
        return repo.findByNombreUsuario(nombre);
    }

    public boolean existe(String nombre) {
        return repo.existsByNombreUsuario(nombre);
    }

    public Usuario guardar(Usuario u) {
        return repo.save(u);
    }
}
