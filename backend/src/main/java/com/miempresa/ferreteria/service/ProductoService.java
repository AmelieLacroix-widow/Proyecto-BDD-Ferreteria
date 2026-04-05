package com.miempresa.ferreteria.service;

import com.miempresa.ferreteria.model.Producto;
import com.miempresa.ferreteria.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository repo;

    public List<Producto> buscar(String texto) {
        return repo.findByDescripcionContainingIgnoreCaseOrCodigoBarrasContaining(texto, texto);
    }

    public List<Producto> porDepartamento(Integer id) {
        return repo.findByDepartamento_IdDepartamento(id);
    }

    public List<Producto> porProveedor(Integer id) {
        return repo.findByProveedor_IdProveedor(id);
    }

    public List<Producto> inventarioActivo() {
        return repo.findByUsaInventarioTrue();
    }

    public Producto guardar(Producto p) {
        return repo.save(p);
    }

    public Producto obtener(String codigo) {
        return repo.findById(codigo).orElse(null);
    }

    public int actualizarExistencia(String codigo, BigDecimal existencia) {
        return repo.actualizarExistencia(codigo, existencia);
    }
}