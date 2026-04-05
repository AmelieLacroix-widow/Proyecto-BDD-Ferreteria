package com.miempresa.ferreteria.service;

import com.miempresa.ferreteria.model.Modulo;
import com.miempresa.ferreteria.repository.ModuloRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para el catálogo de Módulos.
 * Los módulos son fijos del sistema (definidos en el INSERT inicial del SQL)
 * y no se crean ni eliminan desde la aplicación, por lo que este servicio
 * es esencialmente de solo lectura.
 */
@Service
public class ModuloService {

    private final ModuloRepository repo;

    public ModuloService(ModuloRepository repo) {
        this.repo = repo;
    }

    /** Devuelve todos los módulos del sistema. */
    public List<Modulo> todos() {
        return repo.findAll();
    }

    /** Busca un módulo por su ID. */
    public Optional<Modulo> buscarPorId(Integer idModulo) {
        return repo.findById(idModulo);
    }

    /**
     * Busca un módulo por nombre exacto.
     * Usado en UsuarioModuloService para obtener el idModulo
     * a partir del nombre ("Ventas", "Productos", etc.)
     */
    public Optional<Modulo> buscarPorNombre(String nombreModulo) {
        return repo.findByNombreModulo(nombreModulo);
    }
}
