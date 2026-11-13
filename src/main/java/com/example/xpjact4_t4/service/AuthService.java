package com.example.xpjact4_t4.service;

import java.time.Instant;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.xpjact4_t4.dto.LoginRequest;
import com.example.xpjact4_t4.dto.RegistroRequest;
import com.example.xpjact4_t4.dto.TokenResponse;
import com.example.xpjact4_t4.dto.UsuarioDTO;
import com.example.xpjact4_t4.entity.Usuario;
import com.example.xpjact4_t4.exception.DuplicadoException;
import com.example.xpjact4_t4.repository.UsuarioRepository;
import com.example.xpjact4_t4.security.JwtUtil;

@Service
public class AuthService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UsuarioRepository usuarios, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public UsuarioDTO registrar(RegistroRequest datos) {
        String correo = datos.correo().trim().toLowerCase();
        if (usuarios.existsByCorreo(correo)) {
            throw new DuplicadoException("El correo " + correo + " ya está registrado");
        }
        Usuario nuevo = new Usuario();
        nuevo.setNombreCompleto(datos.nombreCompleto().trim());
        nuevo.setCorreo(correo);
        nuevo.setContrasenaHash(passwordEncoder.encode(datos.contrasena()));
        return Convertidor.aDTO(usuarios.save(nuevo));
    }

    /** Si el correo o la contraseña no coinciden, Spring lanza BadCredentialsException (401). */
    public TokenResponse login(LoginRequest datos) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(datos.correo().trim().toLowerCase(), datos.contrasena()));
        Usuario usuario = (Usuario) auth.getPrincipal();
        Instant expira = jwtUtil.calcularExpiracion();
        return new TokenResponse(jwtUtil.generar(usuario, expira), "Bearer", expira, Convertidor.aDTO(usuario));
    }

    public UsuarioDTO perfil(Usuario usuario) {
        return Convertidor.aDTO(usuario);
    }
}
