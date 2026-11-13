package com.example.xpjact4_t4.config;

import java.math.BigDecimal;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.xpjact4_t4.entity.Categoria;
import com.example.xpjact4_t4.entity.Producto;
import com.example.xpjact4_t4.repository.CategoriaRepository;
import com.example.xpjact4_t4.repository.ProductoRepository;

/** La primera vez que arranca (tablas vacías) guarda categorías y productos de ejemplo. */
@Component
public class CargaInicial implements ApplicationRunner {

    private final CategoriaRepository categorias;
    private final ProductoRepository productos;

    public CargaInicial(CategoriaRepository categorias, ProductoRepository productos) {
        this.categorias = categorias;
        this.productos = productos;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (categorias.count() > 0) {
            return;
        }
        Categoria papeleria = categorias.save(new Categoria("Papelería", "Cuadernos, plumas y material escolar"));
        Categoria electronica = categorias.save(new Categoria("Electrónica", "Accesorios para computadora y celular"));
        Categoria cafeteria = categorias.save(new Categoria("Cafetería", "Bebidas y snacks"));

        guardar("PAP-001", "Cuaderno profesional", "100 hojas, cuadro chico", "45.50", 120, papeleria);
        guardar("PAP-002", "Pluma de gel azul", "Punto fino 0.7 mm", "18.00", 300, papeleria);
        guardar("PAP-003", "Marcatextos (4 piezas)", "Colores pastel", "62.90", 80, papeleria);
        guardar("ELE-001", "Mouse inalámbrico", "Receptor USB, 1600 DPI", "249.00", 25, electronica);
        guardar("ELE-002", "Memoria USB 64 GB", "USB 3.0", "159.99", 40, electronica);
        guardar("ELE-003", "Cable USB-C", "1 metro, carga rápida", "89.00", 60, electronica);
        guardar("CAF-001", "Café americano", "Vaso de 12 oz", "35.00", 200, cafeteria);
        guardar("CAF-002", "Galletas de avena", "Paquete de 6", "28.50", 90, cafeteria);
    }

    private void guardar(String codigo, String nombre, String descripcion, String precio, int existencias, Categoria c) {
        Producto p = new Producto();
        p.setCodigo(codigo);
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setPrecio(new BigDecimal(precio));
        p.setExistencias(existencias);
        p.setCategoria(c);
        productos.save(p);
    }
}
