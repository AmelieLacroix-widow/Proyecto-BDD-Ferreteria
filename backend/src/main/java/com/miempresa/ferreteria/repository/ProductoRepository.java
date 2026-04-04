package com.miempresa.ferreteria.repository;

import com.miempresa.ferreteria.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repositorio para la entidad Producto.
 * Tabla: PRODUCTO — PK: codigoBarras (String, no auto-generada)
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, String> {

    /**
     * Búsqueda de catálogo por texto libre en descripción o código de barras.
     * Equivale al campo de búsqueda con lupa de la pantalla de Productos.
     *
     * SELECT * FROM PRODUCTO
     * WHERE descripcion LIKE %?% OR codigo_barras LIKE %?%
     */
    List<Producto> findByDescripcionContainingIgnoreCaseOrCodigoBarrasContaining(
            String descripcion, String codigoBarras);

    /**
     * Filtra el catálogo completo por departamento.
     * Navega por la relación @ManyToOne: departamento.idDepartamento
     *
     * SELECT * FROM PRODUCTO WHERE id_departamento = ?
     */
    List<Producto> findByDepartamento_IdDepartamento(Integer idDepartamento);

    /**
     * Filtra el catálogo completo por proveedor.
     * Navega por la relación @ManyToOne: proveedor.idProveedor
     *
     * SELECT * FROM PRODUCTO WHERE id_proveedor = ?
     */
    List<Producto> findByProveedor_IdProveedor(Integer idProveedor);

    /**
     * Devuelve solo los productos con inventario activo (usa_inventario = TRUE).
     * Base del módulo de Inventario.
     *
     * SELECT * FROM PRODUCTO WHERE usa_inventario = TRUE
     */
    List<Producto> findByUsaInventarioTrue();

    /**
     * Productos con inventario activo, filtrados además por departamento.
     * Combinación del filtro de Inventario + selector de departamento.
     * Navega por la relación @ManyToOne: departamento.idDepartamento
     *
     * SELECT * FROM PRODUCTO WHERE usa_inventario = TRUE AND id_departamento = ?
     */
    List<Producto> findByUsaInventarioTrueAndDepartamento_IdDepartamento(Integer idDepartamento);

    /**
     * Búsqueda por texto dentro de los productos con inventario activo.
     * Permite buscar en la tabla del módulo de Inventario.
     *
     * SELECT * FROM PRODUCTO
     * WHERE usa_inventario = TRUE
     *   AND (descripcion LIKE %?% OR codigo_barras LIKE %?%)
     */
    List<Producto> findByUsaInventarioTrueAndDescripcionContainingIgnoreCaseOrUsaInventarioTrueAndCodigoBarrasContaining(
            String descripcion, String codigoBarras);

    /**
     * Productos cuya existencia está en o por debajo del mínimo configurado.
     * Filtro "Solo mínimos" del reporte de inventario.
     * Solo aplica a productos con inventario activo y con invMinimo definido.
     *
     * SELECT * FROM PRODUCTO
     * WHERE usa_inventario = TRUE
     *   AND inv_minimo IS NOT NULL
     *   AND existencia <= inv_minimo
     */
    @Query("SELECT p FROM Producto p WHERE p.usaInventario = TRUE AND p.invMinimo IS NOT NULL AND p.existencia <= p.invMinimo")
    List<Producto> findProductosBajoMinimo();

    /**
     * Actualiza únicamente el campo existencia de un producto.
     * Más eficiente que cargar toda la entidad para una sola operación de descuento/incremento.
     * Invocado por VentasService al cobrar y por InventarioService en ajustes manuales.
     *
     * UPDATE PRODUCTO SET existencia = :existencia WHERE codigo_barras = :codigoBarras
     */
    @Modifying
    @Transactional
    @Query("UPDATE Producto p SET p.existencia = :existencia WHERE p.codigoBarras = :codigoBarras")
    int actualizarExistencia(@Param("codigoBarras") String codigoBarras,
                             @Param("existencia") BigDecimal existencia);

    /**
     * Suma el valor total del inventario (costo × existencia) para todos los productos activos.
     * Usado en el resumen global del módulo de Inventario.
     *
     * SELECT SUM(precio_costo * existencia) FROM PRODUCTO WHERE usa_inventario = TRUE
     */
    @Query("SELECT COALESCE(SUM(p.precioCosto * p.existencia), 0) FROM Producto p WHERE p.usaInventario = TRUE")
    BigDecimal calcularCostoTotalInventario();

    /**
     * Suma el total de unidades en existencia para todos los productos activos.
     * Usado en el contador "Cantidad de productos en inventario" del módulo de Inventario.
     *
     * SELECT SUM(existencia) FROM PRODUCTO WHERE usa_inventario = TRUE
     */
    @Query("SELECT COALESCE(SUM(p.existencia), 0) FROM Producto p WHERE p.usaInventario = TRUE")
    BigDecimal calcularCantidadTotalInventario();
}
