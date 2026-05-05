package com.miempresa.ferreteria.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "DETALLE_TICKET")
@IdClass(DetalleTicketId.class)
public class DetalleTicket {

    public DetalleTicket() {
    }

    // Nombre del atributo DEBE coincidir con DetalleTicketId.folioTicket
    @Id
    @Column(name = "folio_ticket")
    private Integer folioTicket;

    // Nombre del atributo DEBE coincidir con DetalleTicketId.codigoBarras
    @Id
    @Column(name = "codigo_barras", length = 50)
    private String codigoBarras;

    // FIX: @JsonIgnore en el campo ticket.
    // Sin esto, al serializar un DetalleTicket, Jackson serializa el Ticket
    // completo (con usuario, cliente, ticketReferencia) dentro de CADA renglón.
    // Además de ser datos redundantes (el folio ya está en el campo folioTicket),
    // creaba una cadena de objetos anidados innecesaria y peligrosa.
    // El frontend (Historial.java) no necesita el objeto Ticket aquí; ya lo
    // obtiene con GET /tickets/{folio} en una llamada separada.
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folio_ticket", insertable = false, updatable = false)
    private Ticket ticket;

    // FIX: @JsonIgnoreProperties en el campo producto.
    // Permite que Jackson serialice el objeto Producto correctamente aunque sea
    // un proxy Hibernate (FetchType.LAZY). Sin esto, Jackson lanzaba
    // "No serializer found for class ...HibernateProxy" o dejaba el campo nulo,
    // impidiendo que Historial.java leyera producto.descripcion.
    // La descripción se obtiene mediante el JOIN FETCH agregado en el repositorio.
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codigo_barras", insertable = false, updatable = false)
    private Producto producto;

    // Inventario usa scale=3 para soportar granel (ej. 1.550 kg)
    @Column(name = "cantidad", precision = 10, scale = 3, nullable = false)
    private BigDecimal cantidad;

    @Column(name = "precio_unitario_venta", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioUnitarioVenta;

    @Column(name = "importe", precision = 10, scale = 2, nullable = false)
    private BigDecimal importe;

    @Column(name = "descuento_producto", precision = 5, scale = 3, nullable = false)
    private BigDecimal descuentoProducto;

    // --- Getters y Setters ---

    public Integer getFolioTicket() {
        return folioTicket;
    }

    public void setFolioTicket(Integer folioTicket) {
        this.folioTicket = folioTicket;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitarioVenta() {
        return precioUnitarioVenta;
    }

    public void setPrecioUnitarioVenta(BigDecimal precioUnitarioVenta) {
        this.precioUnitarioVenta = precioUnitarioVenta;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public BigDecimal getDescuentoProducto() {
        return descuentoProducto;
    }

    public void setDescuentoProducto(BigDecimal descuentoProducto) {
        this.descuentoProducto = descuentoProducto;
    }
}