package com.miempresa.ferreteria.repository;

import com.miempresa.ferreteria.model.Corte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Corte.
 * Tabla: CORTE — PK: idCorte (Integer)
 *
 * Registra el cierre de caja diario: totales por método de pago,
 * conteo de billetes, fondo inicial/final y diferencia de caja.
 */
@Repository
public interface CorteRepository extends JpaRepository<Corte, Integer> {

    /**
     * Busca el corte de una fecha específica.
     * Un día normalmente tiene un solo corte. Se usa Optional porque
     * puede no haberse generado aún el día en curso.
     *
     * SELECT * FROM CORTE WHERE fecha_corte = ? LIMIT 1
     */
    Optional<Corte> findFirstByFechaCorte(LocalDate fechaCorte);

    /**
     * Verifica si ya existe un corte para la fecha indicada.
     * Previene generar un corte duplicado para el mismo día.
     *
     * SELECT COUNT(*) > 0 FROM CORTE WHERE fecha_corte = ?
     */
    boolean existsByFechaCorte(LocalDate fechaCorte);

    /**
     * Devuelve cortes de un rango de fechas, ordenados de más reciente a más antiguo.
     * Usado en el módulo de Reportes para consultar el histórico de cortes.
     *
     * SELECT * FROM CORTE
     * WHERE fecha_corte BETWEEN ? AND ?
     * ORDER BY fecha_corte DESC
     */
    List<Corte> findByFechaCorteBetweenOrderByFechaCorteDesc(LocalDate desde, LocalDate hasta);

    /**
     * Devuelve el corte más reciente registrado.
     * Útil para mostrar el último corte al abrir el módulo de Corte.
     *
     * SELECT * FROM CORTE ORDER BY fecha_corte DESC LIMIT 1
     */
    Optional<Corte> findTopByOrderByFechaCorteDesc();

    /**
     * Devuelve todos los cortes realizados por un usuario.
     * Navega por la relación @ManyToOne: usuario.idUsuario
     *
     * SELECT * FROM CORTE WHERE id_usuario = ?
     */
    List<Corte> findByUsuario_IdUsuario(Integer idUsuario);
}
