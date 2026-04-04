package com.miempresa.ferreteria.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente; // PK [cite: 173, 175]

    @Column(length = 100)
    private String nombres; // [cite: 176, 179]

    @Column(name = "apellido_paterno", length = 100)
    private String apellidoPaterno; // [cite: 177, 180]

    @Column(name = "apellido_materno", length = 100)
    private String apellidoMaterno; // [cite: 182, 183]

    @Column(length = 20)
    private String telefono; // [cite: 189, 190]

    @Column(length = 100)
    private String correo; // [cite: 191, 192]

    @Column(length = 200)
    private String domicilio; // [cite: 195, 196]

    @Column(length = 100)
    private String colonia; // [cite: 199, 200]

    @Column(name = "municipio_estado", length = 100)
    private String municipioEstado; // [cite: 203, 204]

    @Column(name = "codigo_postal", length = 10)
    private String codigoPostal; // [cite: 207, 208]

    @Column(columnDefinition = "TEXT")
    private String notas; // [cite: 211, 212]

    @Column(name = "tiene_credito")
    private Boolean tieneCredito; // [cite: 215, 216]

    @Column(name = "limite_credito", precision = 10, scale = 2)
    private BigDecimal limiteCredito; // [cite: 217, 218]

    @Column(name = "saldo_credito", precision = 10, scale = 2)
    private BigDecimal saldoCredito; // [cite: 231, 232]

    // --- Getters y Setters ---

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public String getMunicipioEstado() {
        return municipioEstado;
    }

    public void setMunicipioEstado(String municipioEstado) {
        this.municipioEstado = municipioEstado;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public Boolean getTieneCredito() {
        return tieneCredito;
    }

    public void setTieneCredito(Boolean tieneCredito) {
        this.tieneCredito = tieneCredito;
    }

    public BigDecimal getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(BigDecimal limiteCredito) {
        this.limiteCredito = limiteCredito;
    }

    public BigDecimal getSaldoCredito() {
        return saldoCredito;
    }

    public void setSaldoCredito(BigDecimal saldoCredito) {
        this.saldoCredito = saldoCredito;
    }
}
