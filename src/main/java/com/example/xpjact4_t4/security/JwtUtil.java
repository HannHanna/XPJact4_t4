package com.example.xpjact4_t4.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.xpjact4_t4.entity.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Crea y valida los JWT usando la librería JJWT.
 * Algoritmo: HS256 (HMAC-SHA256) con una clave secreta que solo conoce el servidor.
 */
@Component
public class JwtUtil {

    private static final String EMISOR = "xpjact4_t4-api";

    private final SecretKey clave;
    private final long minutos;

    public JwtUtil(@Value("${jwt.secret}") String secreto, @Value("${jwt.minutos}") long minutos) {
        byte[] bytes = secreto.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET debe tener mínimo 32 caracteres");
        }
        this.clave = Keys.hmacShaKeyFor(bytes);
        this.minutos = minutos;
    }

    /** Genera el token del usuario que acaba de iniciar sesión. */
    public String generar(Usuario usuario, Instant expira) {
        return Jwts.builder()
                .issuer(EMISOR)                          // iss
                .subject(usuario.getCorreo())            // sub
                .issuedAt(new Date())                    // iat
                .expiration(Date.from(expira))           // exp
                .claim("id", usuario.getId())
                .claim("nombre", usuario.getNombreCompleto())
                .claim("rol", usuario.getRol().name())
                .signWith(clave, Jwts.SIG.HS256)
                .compact();
    }

    public Instant calcularExpiracion() {
        return Instant.now().plus(minutos, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.SECONDS);
    }

    /**
     * Verifica firma, emisor y fecha de expiración. Si algo falla lanza JwtException.
     * @return el correo (subject) del dueño del token
     */
    public String validarYObtenerCorreo(String token) throws JwtException {
        Claims claims = Jwts.parser()
                .verifyWith(clave)
                .requireIssuer(EMISOR)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }
}
