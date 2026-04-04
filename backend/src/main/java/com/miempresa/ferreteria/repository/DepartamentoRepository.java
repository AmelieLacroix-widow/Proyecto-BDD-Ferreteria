package com.miempresa.ferreteria.repository;

import com.miempresa.ferreteria.model.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Departamento.
 * Tabla: departamento — PK: idDepartamento (Integer, camelCase)
 */
@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Integer> {

    /**
     * Busca un departamento por nombre exacto.
     * Usado para validar duplicados antes de crear uno nuevo.
     *
     * SELECT * FROM departamento WHERE nombre_departamento = ?
     */
    Optional<Departamento> findByNombreDepartamento(String nombreDepartamento);

    /**
     * Verifica si ya existe un departamento con ese nombre.
     *
     * SELECT COUNT(*) > 0 FROM departamento WHERE nombre_departamento = ?
     */
    boolean existsByNombreDepartamento(String nombreDepartamento);
}
