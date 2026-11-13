package com.example.xpjact4_t4.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.xpjact4_t4.dto.CategoriaDTO;
import com.example.xpjact4_t4.service.ProductoService;

/** Catálogo de categorías (protegido), para saber qué categoriaId usar al crear productos. */
@RestController
public class CategoriaController {

    private final ProductoService service;

    public CategoriaController(ProductoService service) {
        this.service = service;
    }

    @GetMapping("/api/categorias")
    public ResponseEntity<List<CategoriaDTO>> listar() {
        return ResponseEntity.ok(service.categorias());
    }
}
