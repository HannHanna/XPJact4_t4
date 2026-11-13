package com.example.xpjact4_t4.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** GET /api (público): resumen de la API para quien abra la URL base. */
@RestController
public class InfoController {

    @GetMapping("/api")
    public ResponseEntity<Map<String, Object>> info() {
        return ResponseEntity.ok(Map.of(
                "proyecto", "XPJact4_t4 - API de inventario con Spring Security y JWT",
                "autora", "Xana Amalinalli Pérez Jiménez",
                "rutasPublicas", List.of("POST /api/auth/register", "POST /api/auth/login"),
                "rutasConToken", List.of(
                        "GET /api/auth/perfil",
                        "GET /api/productos?pagina=0&tamano=10",
                        "GET /api/productos/{id}",
                        "POST /api/productos",
                        "PUT /api/productos/{id}",
                        "PATCH /api/productos/{id}",
                        "DELETE /api/productos/{id}",
                        "GET /api/categorias")));
    }
}
