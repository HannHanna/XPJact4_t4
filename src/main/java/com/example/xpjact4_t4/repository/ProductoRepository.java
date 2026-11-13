package com.example.xpjact4_t4.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.xpjact4_t4.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Lista paginada con filtros opcionales: si "categoriaId" o "texto" vienen en null, no se filtra por ellos.
     * El EntityGraph trae la categoría en la misma consulta.
     */
    @EntityGraph(attributePaths = "categoria")
    @Query("""
            SELECT p FROM Producto p
            WHERE (:categoriaId IS NULL OR p.categoria.id = :categoriaId)
              AND (:texto IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%')))
            """)
    Page<Producto> buscar(@Param("categoriaId") Long categoriaId, @Param("texto") String texto, Pageable pageable);

    boolean existsByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);
}
