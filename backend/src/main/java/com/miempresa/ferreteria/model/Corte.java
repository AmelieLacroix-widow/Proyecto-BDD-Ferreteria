package com.miempresa.ferreteria.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "corte")
public class Corte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_corte")
    private Integer id_corte; // PK [cite: 24, 25]

    @Column(name = "fecha_corte")
    private LocalDate fecha_corte; // [cite: 26, 27]

    @Column(name = "fondo_inicial")
    private BigDecimal fondo_inicial; // [cite: 28, 29]

    @Column(name = "total_ventas_dia")
    private BigDecimal total_ventas_dia; // [cite: 33, 34]

    @Column(name = "total_ventas_con_dcto")
    private BigDecimal total_ventas_con_dcto; // [cite: 37, 38]

    @Column(name = "total_ventas_sin_dcto")
    private BigDecimal total_ventas_sin_dcto; // [cite: 42, 43]

    @Column(name = "venta_neta")
    private BigDecimal venta_neta; // [cite: 44, 45]

    @Column(name = "total_efectivo_dia")
    private BigDecimal total_efectivo_dia; // [cite: 46, 47]

    @Column(name = "total_tarjeta_dia")
    private BigDecimal total_tarjeta_dia; // [cite: 48, 49]

    @Column(name = "total_transferencia_dia")
    private BigDecimal total_transferencia_dia; // [cite: 50, 51]

    @Column(name = "total_credito_dia")
    private BigDecimal total_credito_dia; // [cite: 52, 53]

    @Column(name = "total_cheque_dia")
    private BigDecimal total_cheque_dia; // [cite: 77, 78]

    @Column(name = "total_gastos_dia")
    private BigDecimal total_gastos_dia; // [cite: 90, 91]

    @Column(name = "fondo_final")
    private BigDecimal fondo_final; // [cite: 93, 94]

    @Column(name = "total_entregado")
    private BigDecimal total_entregado; // [cite: 104, 105]

    @Column(name = "diferencia")
    private BigDecimal diferencia; // [cite: 106, 107]

    // Conteo de billetes y monedas
    private Integer billetes_500; // [cite: 114, 115]
    private Integer billetes_200; // [cite: 118, 119]
    private Integer billetes_100; // [cite: 121, 122]
    private Integer billetes_50; // [cite: 125, 126]
    private Integer billetes_20; // [cite: 129, 130]
    private BigDecimal monedas; // [cite: 134, 135]
    private BigDecimal dolares; // [cite: 138, 139]

    @Column(name = "venta_sin_ticket")
    private BigDecimal venta_sin_ticket; // [cite: 142, 143]

    @Column(name = "entrada_caja")
    private BigDecimal entrada_caja; // [cite: 146, 147]

    @Column(name = "id_usuario")
    private Integer id_usuario; // FK [cite: 40, 41]

    public Integer getId_corte() {
        return id_corte;
    }

    public void setId_corte(Integer id_corte) {
        this.id_corte = id_corte;
    }

    public LocalDate getFecha_corte() {
        return fecha_corte;
    }

    public void setFecha_corte(LocalDate fecha_corte) {
        this.fecha_corte = fecha_corte;
    }

    public BigDecimal getFondo_inicial() {
        return fondo_inicial;
    }

    public void setFondo_inicial(BigDecimal fondo_inicial) {
        this.fondo_inicial = fondo_inicial;
    }

    public BigDecimal getTotal_ventas_dia() {
        return total_ventas_dia;
    }

    public void setTotal_ventas_dia(BigDecimal total_ventas_dia) {
        this.total_ventas_dia = total_ventas_dia;
    }

    public BigDecimal getTotal_ventas_con_dcto() {
        return total_ventas_con_dcto;
    }

    public void setTotal_ventas_con_dcto(BigDecimal total_ventas_con_dcto) {
        this.total_ventas_con_dcto = total_ventas_con_dcto;
    }

    public BigDecimal getTotal_ventas_sin_dcto() {
        return total_ventas_sin_dcto;
    }

    public void setTotal_ventas_sin_dcto(BigDecimal total_ventas_sin_dcto) {
        this.total_ventas_sin_dcto = total_ventas_sin_dcto;
    }

    public BigDecimal getVenta_neta() {
        return venta_neta;
    }

    public void setVenta_neta(BigDecimal venta_neta) {
        this.venta_neta = venta_neta;
    }

    public BigDecimal getTotal_efectivo_dia() {
        return total_efectivo_dia;
    }

    public void setTotal_efectivo_dia(BigDecimal total_efectivo_dia) {
        this.total_efectivo_dia = total_efectivo_dia;
    }

    public BigDecimal getTotal_tarjeta_dia() {
        return total_tarjeta_dia;
    }

    public void setTotal_tarjeta_dia(BigDecimal total_tarjeta_dia) {
        this.total_tarjeta_dia = total_tarjeta_dia;
    }

    public BigDecimal getTotal_transferencia_dia() {
        return total_transferencia_dia;
    }

    public void setTotal_transferencia_dia(BigDecimal total_transferencia_dia) {
        this.total_transferencia_dia = total_transferencia_dia;
    }

    public BigDecimal getTotal_credito_dia() {
        return total_credito_dia;
    }

    public void setTotal_credito_dia(BigDecimal total_credito_dia) {
        this.total_credito_dia = total_credito_dia;
    }

    public BigDecimal getTotal_cheque_dia() {
        return total_cheque_dia;
    }

    public void setTotal_cheque_dia(BigDecimal total_cheque_dia) {
        this.total_cheque_dia = total_cheque_dia;
    }

    public BigDecimal getTotal_gastos_dia() {
        return total_gastos_dia;
    }

    public void setTotal_gastos_dia(BigDecimal total_gastos_dia) {
        this.total_gastos_dia = total_gastos_dia;
    }

    public BigDecimal getFondo_final() {
        return fondo_final;
    }

    public void setFondo_final(BigDecimal fondo_final) {
        this.fondo_final = fondo_final;
    }

    public BigDecimal getTotal_entregado() {
        return total_entregado;
    }

    public void setTotal_entregado(BigDecimal total_entregado) {
        this.total_entregado = total_entregado;
    }

    public BigDecimal getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(BigDecimal diferencia) {
        this.diferencia = diferencia;
    }

    public Integer getBilletes_500() {
        return billetes_500;
    }

    public void setBilletes_500(Integer billetes_500) {
        this.billetes_500 = billetes_500;
    }

    public Integer getBilletes_200() {
        return billetes_200;
    }

    public void setBilletes_200(Integer billetes_200) {
        this.billetes_200 = billetes_200;
    }

    public Integer getBilletes_100() {
        return billetes_100;
    }

    public void setBilletes_100(Integer billetes_100) {
        this.billetes_100 = billetes_100;
    }

    public Integer getBilletes_50() {
        return billetes_50;
    }

    public void setBilletes_50(Integer billetes_50) {
        this.billetes_50 = billetes_50;
    }

    public Integer getBilletes_20() {
        return billetes_20;
    }

    public void setBilletes_20(Integer billetes_20) {
        this.billetes_20 = billetes_20;
    }

    public BigDecimal getMonedas() {
        return monedas;
    }

    public void setMonedas(BigDecimal monedas) {
        this.monedas = monedas;
    }

    public BigDecimal getDolares() {
        return dolares;
    }

    public void setDolares(BigDecimal dolares) {
        this.dolares = dolares;
    }

    public BigDecimal getVenta_sin_ticket() {
        return venta_sin_ticket;
    }

    public void setVenta_sin_ticket(BigDecimal venta_sin_ticket) {
        this.venta_sin_ticket = venta_sin_ticket;
    }

    public BigDecimal getEntrada_caja() {
        return entrada_caja;
    }

    public void setEntrada_caja(BigDecimal entrada_caja) {
        this.entrada_caja = entrada_caja;
    }

    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }

}

