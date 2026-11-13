package com.example.xpjact4_t4.service;

import com.example.xpjact4_t4.dto.CategoriaDTO;
import com.example.xpjact4_t4.dto.ProductoDTO;
import com.example.xpjact4_t4.dto.UsuarioDTO;
import com.example.xpjact4_t4.entity.Categoria;
import com.example.xpjact4_t4.entity.Producto;
import com.example.xpjact4_t4.entity.Usuario;

/** Pasa de entidades (base de datos) a DTOs (lo que ve el cliente de la API). */
final class Convertidor {

    private Convertidor() {
    }

    static UsuarioDTO aDTO(Usuario u) {
        return new UsuarioDTO(u.getId(), u.getNombreCompleto(), u.getCorreo(), u.getRol().name(), u.getFechaRegistro());
    }

    static CategoriaDTO aDTO(Categoria c) {
        return new CategoriaDTO(c.getId(), c.getNombre(), c.getDescripcion());
    }

    static ProductoDTO aDTO(Producto p) {
        return new ProductoDTO(p.getId(), p.getCodigo(), p.getNombre(), p.getDescripcion(), p.getPrecio(),
                p.getExistencias(), aDTO(p.getCategoria()), p.getCreado(), p.getModificado());
    }
}
