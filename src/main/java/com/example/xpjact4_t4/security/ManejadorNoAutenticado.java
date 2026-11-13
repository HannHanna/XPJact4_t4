package com.example.xpjact4_t4.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.example.xpjact4_t4.exception.NoAutenticadoException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Se ejecuta cuando alguien sin un token válido pide una ruta protegida.
 * En lugar de escribir la respuesta aquí, se la pasa al @RestControllerAdvice
 * (ManejadorGlobalErrores) para que el 401 tenga el mismo formato JSON que los demás errores.
 */
@Component
public class ManejadorNoAutenticado implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver resolver;

    public ManejadorNoAutenticado(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) {
        Object errorToken = request.getAttribute(JwtAuthFilter.ATRIBUTO_ERROR);
        String detalle = errorToken != null
                ? errorToken.toString()
                : "Esta ruta requiere iniciar sesión. Manda el header Authorization: Bearer <token>";
        resolver.resolveException(request, response, null, new NoAutenticadoException(detalle));
    }
}
