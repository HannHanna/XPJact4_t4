package com.example.xpjact4_t4.security;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro que se ejecuta una vez en cada petición:
 * 1. Busca el header "Authorization: Bearer <token>".
 * 2. Valida el token con JwtUtil.
 * 3. Si es válido, registra al usuario como autenticado en el SecurityContext.
 * Si no hay token o es inválido, no autentica a nadie; Spring Security responde 401
 * cuando la ruta está protegida (ver ManejadorNoAutenticado).
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String ATRIBUTO_ERROR = "jwt.error";
    private static final String PREFIJO = "Bearer ";

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(PREFIJO)) {
            String token = header.substring(PREFIJO.length()).trim();
            try {
                String correo = jwtUtil.validarYObtenerCorreo(token);
                UserDetails usuario = userDetailsService.loadUserByUsername(correo);

                var autenticacion = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(autenticacion);
            } catch (JwtException | IllegalArgumentException e) {
                request.setAttribute(ATRIBUTO_ERROR, "El token no es válido o ya expiró");
            } catch (UsernameNotFoundException e) {
                request.setAttribute(ATRIBUTO_ERROR, "El usuario del token ya no existe");
            }
        }

        chain.doFilter(request, response);
    }
}
