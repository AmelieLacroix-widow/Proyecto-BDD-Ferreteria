package com.miempresa.ferreteria.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "PRODUCTO")
public class Producto {

    @Id
    @Column(name = "codigo_barras", length = 50)
    private String codigoBarras;

    @Column(name = "descripcion", length = 200, nullable = false)
    private String descripcion;

    @Column(name = "precio_costo", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioCosto;

    @Column(name = "porcentaje_ganancia", precision = 5, scale = 2, nullable = false)
    private BigDecimal porcentajeGanancia;

    @Column(name = "precio_venta_lista", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioVentaLista;

    // Inventario usa scale=3 para soportar granel (ej. 1.550 kg)
    @Column(name = "existencia", precision = 10, scale = 3, nullable = false)
    private BigDecimal existencia;

    @Column(name = "inv_minimo", precision = 10, scale = 3)
    private BigDecimal invMinimo;

    @Column(name = "inv_maximo", precision = 10, scale = 3)
    private BigDecimal invMaximo;

    // Solo acepta 'Pza' o 'Granel' según el CHECK del SQL
    @Column(name = "unidad", length = 10, nullable = false)
    private String unidad;

    @Column(name = "usa_inventario", nullable = false)
    private Boolean usaInventario;

    @Column(name = "cfdi_clave_producto", length = 20)
    private String cfdiClaveProducto;

    @Column(name = "cfdi_unidad_medida", length = 20)
    private String cfdiUnidadMedida;

    // Relación ManyToOne con Departamento (antes era solo un Integer)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_departamento")
    private Departamento departamento;

    // Relación ManyToOne con Proveedor (antes era solo un Integer)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;

    // --- Getters y Setters ---

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioCosto() {
        return precioCosto;
    }

    public void setPrecioCosto(BigDecimal precioCosto) {
        this.precioCosto = precioCosto;
    }

    public BigDecimal getPorcentajeGanancia() {
        return porcentajeGanancia;
    }

    public void setPorcentajeGanancia(BigDecimal porcentajeGanancia) {
        this.porcentajeGanancia = porcentajeGanancia;
    }

    public BigDecimal getPrecioVentaLista() {
        return precioVentaLista;
    }

    public void setPrecioVentaLista(BigDecimal precioVentaLista) {
        this.precioVentaLista = precioVentaLista;
    }

    public BigDecimal getExistencia() {
        return existencia;
    }

    public void setExistencia(BigDecimal existencia) {
        this.existencia = existencia;
    }

    public BigDecimal getInvMinimo() {
        return invMinimo;
    }

    public void setInvMinimo(BigDecimal invMinimo) {
        this.invMinimo = invMinimo;
    }

    public BigDecimal getInvMaximo() {
        return invMaximo;
    }

    public void setInvMaximo(BigDecimal invMaximo) {
        this.invMaximo = invMaximo;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public Boolean getUsaInventario() {
        return usaInventario;
    }

    public void setUsaInventario(Boolean usaInventario) {
        this.usaInventario = usaInventario;
    }

    public String getCfdiClaveProducto() {
        return cfdiClaveProducto;
    }

    public void setCfdiClaveProducto(String cfdiClaveProducto) {
        this.cfdiClaveProducto = cfdiClaveProducto;
    }

    public String getCfdiUnidadMedida() {
        return cfdiUnidadMedida;
    }

    public void setCfdiUnidadMedida(String cfdiUnidadMedida) {
        this.cfdiUnidadMedida = cfdiUnidadMedida;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }
}
