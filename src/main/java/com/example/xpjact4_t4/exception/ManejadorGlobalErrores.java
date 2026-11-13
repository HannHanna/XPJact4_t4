package com.example.xpjact4_t4.exception;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.xpjact4_t4.dto.ApiError;
import com.example.xpjact4_t4.dto.ApiError.CampoInvalido;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Traduce las excepciones a respuestas JSON con el código HTTP adecuado:
 * 400 JSON mal formado · 401 sin token / login incorrecto · 404 no existe
 * 405 método no permitido · 409 duplicado · 422 validación fallida
 */
@RestControllerAdvice
public class ManejadorGlobalErrores {

    // 422: los datos llegaron bien formados pero no pasan las reglas de @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validacion(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<CampoInvalido> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> new CampoInvalido(f.getField(), f.getDefaultMessage()))
                .toList();
        HttpStatus status = HttpStatus.UNPROCESSABLE_CONTENT;
        return ResponseEntity.status(status).body(new ApiError(LocalDateTime.now(), status.value(),
                status.getReasonPhrase(), "Revisa los campos marcados", req.getRequestURI(), campos));
    }

    // 400: el cuerpo no es un JSON válido o un parámetro tiene el tipo equivocado
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiError> peticionMalFormada(Exception ex, HttpServletRequest req) {
        return error(HttpStatus.BAD_REQUEST, "La petición no tiene un formato válido", req);
    }

    // 401: login con correo o contraseña incorrectos
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> credencialesIncorrectas(BadCredentialsException ex, HttpServletRequest req) {
        return error(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos", req);
    }

    // 401: ruta protegida sin token o con token inválido (viene de ManejadorNoAutenticado)
    @ExceptionHandler(NoAutenticadoException.class)
    public ResponseEntity<ApiError> noAutenticado(NoAutenticadoException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header("WWW-Authenticate", "Bearer")
                .body(ApiError.simple(401, HttpStatus.UNAUTHORIZED.getReasonPhrase(), ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(NoEncontradoException.class)
    public ResponseEntity<ApiError> noEncontrado(NoEncontradoException ex, HttpServletRequest req) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> rutaInexistente(NoResourceFoundException ex, HttpServletRequest req) {
        return error(HttpStatus.NOT_FOUND, "Esta ruta no existe en la API", req);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> metodoNoPermitido(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return error(HttpStatus.METHOD_NOT_ALLOWED, "La ruta no acepta el método " + ex.getMethod(), req);
    }

    @ExceptionHandler(DuplicadoException.class)
    public ResponseEntity<ApiError> duplicado(DuplicadoException ex, HttpServletRequest req) {
        return error(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String detalle, HttpServletRequest req) {
        return ResponseEntity.status(status)
                .body(ApiError.simple(status.value(), status.getReasonPhrase(), detalle, req.getRequestURI()));
    }
}
