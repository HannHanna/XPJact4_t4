package com.example.xpjact4_t4.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.xpjact4_t4.repository.UsuarioRepository;

@Configuration
public class SecurityConfig {

    /**
     * Reglas de seguridad:
     * - Sin sesiones (STATELESS): cada petición se identifica solo con su JWT.
     * - Públicas: registro, login y GET /api (info).
     * - Todo lo demás requiere un token válido.
     * - JwtAuthFilter corre antes del filtro de usuario/contraseña de Spring.
     */
    @Bean
    SecurityFilterChain cadenaDeFiltros(HttpSecurity http, JwtAuthFilter jwtAuthFilter,
                                        ManejadorNoAutenticado manejadorNoAutenticado) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reglas -> reglas
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e.authenticationEntryPoint(manejadorNoAutenticado))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /** Evita que Spring Boot registre JwtAuthFilter una segunda vez como filtro normal del servidor. */
    @Bean
    FilterRegistrationBean<JwtAuthFilter> noRegistrarDosVeces(JwtAuthFilter filtro) {
        FilterRegistrationBean<JwtAuthFilter> registro = new FilterRegistrationBean<>(filtro);
        registro.setEnabled(false);
        return registro;
    }

    /** BCrypt: las contraseñas se guardan como hash, nunca en texto plano. */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /** Spring Security busca al usuario por su correo (Usuario implementa UserDetails). */
    @Bean
    UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return correo -> usuarioRepository.findByCorreo(correo.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("No existe el usuario " + correo));
    }

    /** Compara la contraseña del login contra el hash guardado en la base de datos. */
    @Bean
    AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder encoder) {
        DaoAuthenticationProvider proveedor = new DaoAuthenticationProvider(userDetailsService);
        proveedor.setPasswordEncoder(encoder);
        return new ProviderManager(proveedor);
    }
}
