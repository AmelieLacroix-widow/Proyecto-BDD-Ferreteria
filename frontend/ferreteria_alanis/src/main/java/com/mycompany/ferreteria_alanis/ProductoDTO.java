package com.mycompany.ferreteria_alanis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/**
 * Espejo del modelo Producto del backend.
 * Jackson usa esta clase para convertir el JSON de la API en objetos Java.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductoDTO {

    private String codigoBarras;
    private String descripcion;
    private BigDecimal precioCosto;
    private BigDecimal porcentajeGanancia;
    private BigDecimal precioVentaLista;
    private BigDecimal existencia;
    private BigDecimal invMinimo;
    private BigDecimal invMaximo;
    private String unidad;
    private Boolean usaInventario;
    private String cfdiClaveProducto;
    private String cfdiUnidadMedida;
    private DepartamentoRef departamento;
    private ProveedorRef proveedor;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DepartamentoRef {
        private Integer idDepartamento;
        private String nombreDepartamento;

        public DepartamentoRef() {}
        public DepartamentoRef(Integer idDepartamento) { this.idDepartamento = idDepartamento; }

        public Integer getIdDepartamento() { return idDepartamento; }
        public void setIdDepartamento(Integer id) { this.idDepartamento = id; }
        public String getNombreDepartamento() { return nombreDepartamento; }
        public void setNombreDepartamento(String n) { this.nombreDepartamento = n; }

        @Override
        public String toString() { return nombreDepartamento != null ? nombreDepartamento : ""; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProveedorRef {
        private Integer idProveedor;
        private String nombreProveedor;

        public ProveedorRef() {}
        public ProveedorRef(Integer idProveedor) { this.idProveedor = idProveedor; }

        public Integer getIdProveedor() { return idProveedor; }
        public void setIdProveedor(Integer id) { this.idProveedor = id; }
        public String getNombreProveedor() { return nombreProveedor; }
        public void setNombreProveedor(String n) { this.nombreProveedor = n; }

        @Override
        public String toString() { return nombreProveedor != null ? nombreProveedor : ""; }
    }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String c) { this.codigoBarras = c; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String d) { this.descripcion = d; }
    public BigDecimal getPrecioCosto() { return precioCosto; }
    public void setPrecioCosto(BigDecimal p) { this.precioCosto = p; }
    public BigDecimal getPorcentajeGanancia() { return porcentajeGanancia; }
    public void setPorcentajeGanancia(BigDecimal p) { this.porcentajeGanancia = p; }
    public BigDecimal getPrecioVentaLista() { return precioVentaLista; }
    public void setPrecioVentaLista(BigDecimal p) { this.precioVentaLista = p; }
    public BigDecimal getExistencia() { return existencia; }
    public void setExistencia(BigDecimal e) { this.existencia = e; }
    public BigDecimal getInvMinimo() { return invMinimo; }
    public void setInvMinimo(BigDecimal i) { this.invMinimo = i; }
    public BigDecimal getInvMaximo() { return invMaximo; }
    public void setInvMaximo(BigDecimal i) { this.invMaximo = i; }
    public String getUnidad() { return unidad; }
    public void setUnidad(String u) { this.unidad = u; }
    public Boolean getUsaInventario() { return usaInventario; }
    public void setUsaInventario(Boolean u) { this.usaInventario = u; }
    public String getCfdiClaveProducto() { return cfdiClaveProducto; }
    public void setCfdiClaveProducto(String c) { this.cfdiClaveProducto = c; }
    public String getCfdiUnidadMedida() { return cfdiUnidadMedida; }
    public void setCfdiUnidadMedida(String c) { this.cfdiUnidadMedida = c; }
    public DepartamentoRef getDepartamento() { return departamento; }
    public void setDepartamento(DepartamentoRef d) { this.departamento = d; }
    public ProveedorRef getProveedor() { return proveedor; }
    public void setProveedor(ProveedorRef p) { this.proveedor = p; }
}
