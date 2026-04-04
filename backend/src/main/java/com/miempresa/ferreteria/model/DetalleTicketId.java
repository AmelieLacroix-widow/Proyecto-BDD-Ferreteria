package com.miempresa.ferreteria.model;

import java.io.Serializable;
import java.util.Objects;

public class DetalleTicketId implements Serializable {

    // Los nombres DEBEN ser idénticos a los atributos @Id de DetalleTicket
    private Integer folioTicket;
    private String codigoBarras;

    public DetalleTicketId() {}

    public DetalleTicketId(Integer folioTicket, String codigoBarras) {
        this.folioTicket = folioTicket;
        this.codigoBarras = codigoBarras;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetalleTicketId that = (DetalleTicketId) o;
        return Objects.equals(folioTicket, that.folioTicket) &&
               Objects.equals(codigoBarras, that.codigoBarras);
    }

    @Override
    public int hashCode() {
        return Objects.hash(folioTicket, codigoBarras);
    }
}
