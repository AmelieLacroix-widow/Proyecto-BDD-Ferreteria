package com.miempresa.ferreteria.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @Column(name = "codigo_barras", length = 50)
    private String codigoBarras; // PK

    @Column(length = 200)
    private String descripcion;

    @Column(name = "precio_costo")
    private BigDecimal precioCosto;

    @Column(name = "porcentaje_ganancia")
    private BigDecimal porcentajeGanancia;

    @Column(name = "precio_venta_lista")
    private BigDecimal precioVentaLista;

    private BigDecimal existencia;

    @Column(name = "inv_minimo")
    private BigDecimal invMinimo;

    @Column(name = "inv_maximo")
    private BigDecimal invMaximo;

    @Column(length = 10)
    private String unidad;

    @Column(name = "usa_inventario")
    private Boolean usaInventario;

    @Column(name = "cfdi_clave_producto", length = 20)
    private String cfdiClaveProducto;

    @Column(name = "cfdi_unidad_medida", length = 20)
    private String cfdiUnidadMedida;

    @Column(name = "id_departamento")
    private Integer idDepartamento; // FK

    @Column(name = "id_proveedor")
    private Integer idProveedor; // FK

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

    public Integer getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Integer idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

}