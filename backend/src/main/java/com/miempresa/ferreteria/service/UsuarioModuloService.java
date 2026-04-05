package com.miempresa.ferreteria.service;

import com.miempresa.ferreteria.model.UsuarioModulo;
import com.miempresa.ferreteria.model.UsuarioModuloId;
import com.miempresa.ferreteria.repository.UsuarioModuloRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioModuloService {

    private final UsuarioModuloRepository repo;

    public UsuarioModuloService(UsuarioModuloRepository repo) {
        this.repo = repo;
    }

    /** Devuelve todos los registros de módulos asignados a un usuario. */
    public List<UsuarioModulo> modulosDeUsuario(Integer idUsuario) {
        return repo.findByIdUsuario(idUsuario);
    }

    /** Devuelve los nombres de los módulos autorizados para un usuario (para LoginResponse). */
    public List<String> nombresModulos(Integer idUsuario) {
        return repo.findNombresModulosByIdUsuario(idUsuario);
    }

    /** Verifica si un usuario tiene permiso para acceder a un módulo. */
    public boolean tieneAcceso(Integer idUsuario, Integer idModulo) {
        return repo.existsByIdUsuarioAndIdModulo(idUsuario, idModulo);
    }

    /** Asigna un módulo a un usuario. */
    public UsuarioModulo asignar(Integer idUsuario, Integer idModulo) {
        UsuarioModulo um = new UsuarioModulo();
        um.setIdUsuario(idUsuario);
        um.setIdModulo(idModulo);
        return repo.save(um);
    }

    /** Revoca un módulo específico de un usuario. */
    public void revocar(Integer idUsuario, Integer idModulo) {
        repo.deleteById(new UsuarioModuloId(idUsuario, idModulo));
    }

    /**
     * Revoca todos los módulos de un usuario.
     * Llamado por UsuarioService.eliminar() o al resetear permisos.
     */
    public void revocarTodos(Integer idUsuario) {
        repo.deleteByIdUsuario(idUsuario);
    }
}
