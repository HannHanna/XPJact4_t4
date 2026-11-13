package com.example.xpjact4_t4;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Recorre el flujo completo: registro, login, CRUD con token y rechazos 401/404/409/422. */
@SpringBootTest
@AutoConfigureMockMvc
class ApiSeguridadTest {

    @Autowired
    MockMvc mvc;

    private String json(String texto) {
        return texto.replace('\'', '"');
    }

    private String extraer(String cuerpo, String campo) {
        Matcher m = Pattern.compile("\"" + campo + "\"\\s*:\\s*\"?([^\",}]+)").matcher(cuerpo);
        if (!m.find()) {
            throw new AssertionError("No viene el campo " + campo + " en " + cuerpo);
        }
        return m.group(1);
    }

    @Test
    void flujoCompleto() throws Exception {
        String registro = json("{'nombreCompleto':'Xana Pérez','correo':'xana@prueba.com','contrasena':'Inventario2026'}");

        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(registro))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.correo").value("xana@prueba.com"))
                .andExpect(jsonPath("$.contrasena").doesNotExist())
                .andExpect(jsonPath("$.contrasenaHash").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist());

        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(registro))
                .andExpect(status().isConflict());

        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json("{'correo':'xana@prueba.com','contrasena':'Incorrecta123'}")))
                .andExpect(status().isUnauthorized());

        String login = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json("{'correo':'xana@prueba.com','contrasena':'Inventario2026'}")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn().getResponse().getContentAsString();
        String bearer = "Bearer " + extraer(login, "accessToken");

        // Sin token y con token falso -> 401
        mvc.perform(get("/api/productos")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value(401));
        mvc.perform(get("/api/productos").header("Authorization", "Bearer abc.def.ghi"))
                .andExpect(status().isUnauthorized());

        mvc.perform(get("/api/auth/perfil").header("Authorization", bearer))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombreCompleto").value("Xana Pérez"));

        mvc.perform(get("/api/productos?pagina=0&tamano=3&orden=precio&dir=desc").header("Authorization", bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.length()").value(3))
                .andExpect(jsonPath("$.totalElementos").value(8));

        // Validación -> 422
        mvc.perform(post("/api/productos").header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON)
                        .content(json("{'codigo':'malo','nombre':'','precio':-5,'existencias':-1}")))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.campos[*].campo", hasItem("codigo")));

        String creado = mvc.perform(post("/api/productos").header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("{'codigo':'PAP-100','nombre':'Lápiz','precio':9.50,'existencias':50,'categoriaId':1}")))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getContentAsString();
        String id = extraer(creado, "id");

        mvc.perform(get("/api/productos/" + id).header("Authorization", bearer))
                .andExpect(status().isOk()).andExpect(jsonPath("$.categoria.nombre").value("Papelería"));

        mvc.perform(put("/api/productos/" + id).header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON)
                        .content(json("{'codigo':'PAP-100','nombre':'Lápiz HB','precio':11,'existencias':45,'categoriaId':1}")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Lápiz HB"));

        mvc.perform(patch("/api/productos/" + id).header("Authorization", bearer).contentType(MediaType.APPLICATION_JSON)
                        .content(json("{'existencias':10}")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.existencias").value(10))
                .andExpect(jsonPath("$.nombre").value("Lápiz HB"));

        mvc.perform(delete("/api/productos/" + id).header("Authorization", bearer))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/productos/" + id).header("Authorization", bearer))
                .andExpect(status().isNotFound());

        mvc.perform(get("/api/categorias").header("Authorization", bearer))
                .andExpect(status().isOk()).andExpect(jsonPath("$[*].nombre", not(hasItem("x"))));

        mvc.perform(get("/api")).andExpect(status().isOk());
    }
}
