package com.example.xpjact4_t4.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.xpjact4_t4.dto.LoginRequest;
import com.example.xpjact4_t4.dto.RegistroRequest;
import com.example.xpjact4_t4.dto.TokenResponse;
import com.example.xpjact4_t4.dto.UsuarioDTO;
import com.example.xpjact4_t4.entity.Usuario;
import com.example.xpjact4_t4.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** Público → 201 Created (la respuesta no lleva la contraseña) */
    @PostMapping("/register")
    public ResponseEntity<UsuarioDTO> registrar(@Valid @RequestBody RegistroRequest datos) {
        UsuarioDTO usuario = authService.registrar(datos);
        return ResponseEntity.created(URI.create("/api/auth/perfil")).body(usuario);
    }

    /** Público → 200 OK con el JWT · 401 si las credenciales no coinciden */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest datos) {
        return ResponseEntity.ok(authService.login(datos));
    }

    /** Protegido → datos del usuario dueño del token */
    @GetMapping("/perfil")
    public ResponseEntity<UsuarioDTO> perfil(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(authService.perfil(usuario));
    }
}
