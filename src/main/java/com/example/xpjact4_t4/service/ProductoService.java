package com.example.xpjact4_t4.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.xpjact4_t4.dto.CategoriaDTO;
import com.example.xpjact4_t4.dto.PaginaDTO;
import com.example.xpjact4_t4.dto.ProductoDTO;
import com.example.xpjact4_t4.dto.ProductoPatchRequest;
import com.example.xpjact4_t4.dto.ProductoRequest;
import com.example.xpjact4_t4.entity.Categoria;
import com.example.xpjact4_t4.entity.Producto;
import com.example.xpjact4_t4.exception.DuplicadoException;
import com.example.xpjact4_t4.exception.NoEncontradoException;
import com.example.xpjact4_t4.repository.CategoriaRepository;
import com.example.xpjact4_t4.repository.ProductoRepository;

/** Reglas de negocio del inventario. Recibe y regresa DTOs. */
@Service
public class ProductoService {

    private final ProductoRepository productos;
    private final CategoriaRepository categorias;

    public ProductoService(ProductoRepository productos, CategoriaRepository categorias) {
        this.productos = productos;
        this.categorias = categorias;
    }

    @Transactional(readOnly = true)
    public PaginaDTO<ProductoDTO> listar(Long categoriaId, String buscar, Pageable pageable) {
        String texto = (buscar == null || buscar.isBlank()) ? null : buscar.trim();
        return PaginaDTO.desde(productos.buscar(categoriaId, texto, pageable).map(Convertidor::aDTO));
    }

    @Transactional(readOnly = true)
    public ProductoDTO obtener(Long id) {
        return Convertidor.aDTO(buscarProducto(id));
    }

    @Transactional
    public ProductoDTO crear(ProductoRequest datos) {
        if (productos.existsByCodigoIgnoreCase(datos.codigo())) {
            throw new DuplicadoException("Ya hay un producto con el código " + datos.codigo());
        }
        Producto producto = new Producto();
        llenar(producto, datos);
        return Convertidor.aDTO(productos.save(producto));
    }

    @Transactional
    public ProductoDTO reemplazar(Long id, ProductoRequest datos) {
        Producto producto = buscarProducto(id);
        if (productos.existsByCodigoIgnoreCaseAndIdNot(datos.codigo(), id)) {
            throw new DuplicadoException("Ya hay otro producto con el código " + datos.codigo());
        }
        llenar(producto, datos);
        return Convertidor.aDTO(productos.saveAndFlush(producto));
    }

    @Transactional
    public ProductoDTO modificarParcial(Long id, ProductoPatchRequest cambios) {
        Producto producto = buscarProducto(id);
        if (cambios.precio() != null) {
            producto.setPrecio(cambios.precio());
        }
        if (cambios.existencias() != null) {
            producto.setExistencias(cambios.existencias());
        }
        return Convertidor.aDTO(productos.saveAndFlush(producto));
    }

    @Transactional
    public void eliminar(Long id) {
        productos.delete(buscarProducto(id));
    }

    @Transactional(readOnly = true)
    public List<CategoriaDTO> categorias() {
        return categorias.findAll(Sort.by("nombre")).stream().map(Convertidor::aDTO).toList();
    }

    private Producto buscarProducto(Long id) {
        return productos.findById(id).orElseThrow(() -> new NoEncontradoException("El producto", id));
    }

    private void llenar(Producto producto, ProductoRequest datos) {
        Categoria categoria = categorias.findById(datos.categoriaId())
                .orElseThrow(() -> new NoEncontradoException("La categoría", datos.categoriaId()));
        producto.setCodigo(datos.codigo().toUpperCase());
        producto.setNombre(datos.nombre().trim());
        producto.setDescripcion(datos.descripcion() == null || datos.descripcion().isBlank() ? null : datos.descripcion().trim());
        producto.setPrecio(datos.precio());
        producto.setExistencias(datos.existencias());
        producto.setCategoria(categoria);
    }
}
