package com.miempresa.ferreteria.model; 

import jakarta.persistence.*; 
import java.math.BigDecimal;

@Entity //
@Table(name = "producto")
public class Producto {

    @Id //
    @Column(name = "codigo_barras")
    private String codigoBarras; // PK

    @Column(name = "descripcion")
    private String descripcion; 

    @Column(name = "precio_costo")
    private BigDecimal precioCosto; 

    @Column(name = "precio_venta_lista")
    private BigDecimal precioVentaLista; 

    @Column(name = "existencia")
    private BigDecimal existencia; 

    @Column(name = "unidad")
    private String unidad; 
}
