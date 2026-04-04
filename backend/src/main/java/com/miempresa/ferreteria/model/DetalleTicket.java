package com.miempresa.ferreteria.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_ticket")
@IdClass(DetalleTicketId.class)
public class DetalleTicket {

    @Id
    @Column(name = "folio_ticket")
    private Integer folio_ticket; // PK FK [cite: 83, 96]

    @Id
    @Column(name = "codigo_barras", length = 50)
    private String codigo_barras; // PK FK [cite: 86, 97]

    @Column(name = "cantidad", precision = 10, scale = 3)
    private BigDecimal cantidad; // [cite: 88, 89, 98]

    @Column(name = "precio_unitario_venta", precision = 10, scale = 2)
    private BigDecimal precio_unitario_venta; // [cite: 95, 99]

    @Column(name = "importe", precision = 10, scale = 2)
    private BigDecimal importe; // [cite: 100, 101]

    @Column(name = "descuento_producto", precision = 5, scale = 3)
    private BigDecimal descuento_producto; // [cite: 102, 103]

    // --- Getters y Setters ---

    public Integer getFolio_ticket() { return folio_ticket; }
    public void setFolio_ticket(Integer folio_ticket) { this.folio_ticket = folio_ticket; }

    public String getCodigo_barras() { return codigo_barras; }
    public void setCodigo_barras(String codigo_barras) { this.codigo_barras = codigo_barras; }

    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecio_unitario_venta() { return precio_unitario_venta; }
    public void setPrecio_unitario_venta(BigDecimal precio_unitario_venta) { this.precio_unitario_venta = precio_unitario_venta; }

    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }

    public BigDecimal getDescuento_producto() { return descuento_producto; }
    public void setDescuento_producto(BigDecimal descuento_producto) { this.descuento_producto = descuento_producto; }
}
