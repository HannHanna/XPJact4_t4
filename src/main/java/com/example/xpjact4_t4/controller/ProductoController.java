package com.example.xpjact4_t4.controller;

import java.net.URI;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.xpjact4_t4.dto.PaginaDTO;
import com.example.xpjact4_t4.dto.ProductoDTO;
import com.example.xpjact4_t4.dto.ProductoPatchRequest;
import com.example.xpjact4_t4.dto.ProductoRequest;
import com.example.xpjact4_t4.service.ProductoService;

import jakarta.validation.Valid;

/** CRUD de productos. Todas las rutas requieren el header Authorization: Bearer <token>. */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private static final Set<String> ORDENABLES = Set.of("id", "codigo", "nombre", "precio", "existencias");

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    /** GET /api/productos?pagina=0&tamano=10&orden=precio&dir=desc&categoriaId=1&buscar=mouse */
    @GetMapping
    public ResponseEntity<PaginaDTO<ProductoDTO>> listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamano,
            @RequestParam(defaultValue = "id") String orden,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String buscar) {

        String campo = ORDENABLES.contains(orden) ? orden : "id";
        Sort sort = "desc".equalsIgnoreCase(dir) ? Sort.by(campo).descending() : Sort.by(campo).ascending();
        var pageable = PageRequest.of(Math.max(pagina, 0), Math.clamp(tamano, 1, 100), sort);
        return ResponseEntity.ok(service.listar(categoriaId, buscar, pageable));
    }

    /** GET /api/productos/{id} → 200 · 404 */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> verUno(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    /** POST /api/productos → 201 Created + header Location */
    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody ProductoRequest datos) {
        ProductoDTO creado = service.crear(datos);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    /** PUT /api/productos/{id} → reemplaza todos los datos · 200 */
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> reemplazar(@PathVariable Long id, @Valid @RequestBody ProductoRequest datos) {
        return ResponseEntity.ok(service.reemplazar(id, datos));
    }

    /** PATCH /api/productos/{id} → cambia solo precio y/o existencias · 200 */
    @PatchMapping("/{id}")
    public ResponseEntity<ProductoDTO> modificar(@PathVariable Long id, @Valid @RequestBody ProductoPatchRequest cambios) {
        return ResponseEntity.ok(service.modificarParcial(id, cambios));
    }

    /** DELETE /api/productos/{id} → 204 No Content */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
