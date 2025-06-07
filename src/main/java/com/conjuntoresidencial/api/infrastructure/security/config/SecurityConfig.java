package com.conjuntoresidencial.api.infrastructure.security.config;

import com.conjuntoresidencial.api.infrastructure.security.jwt.JwtAuthenticationEntryPoint;
import com.conjuntoresidencial.api.infrastructure.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService; // Viene de Spring Security
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration // Indica que esta clase contiene configuraciones de beans para Spring
@EnableWebSecurity // Habilita la configuración de seguridad web de Spring Security
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true) // Habilita la seguridad a nivel de método (ej. @Secured, @RolesAllowed)
@RequiredArgsConstructor // Lombok para inyección de dependencias
public class SecurityConfig {

    // UserDetailsService se inyectará automáticamente gracias a nuestra implementación UserDetailsServiceImpl
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint unauthorizedHandler; // Para manejar errores de autenticación 401
    private final JwtAuthenticationFilter jwtAuthenticationFilter; // Nuestro filtro JWT personalizado

    // URLs públicas que no requieren autenticación
    private static final String[] PUBLIC_URLS = {
            "/api/v1/auth/**", // Endpoints de autenticación (login, registro)
            "/swagger-ui/**",     // Swagger UI
            "/v3/api-docs/**",    // Documentación OpenAPI
            // Añade aquí cualquier otro endpoint público que necesites
    };

    @Bean // Expone el AuthenticationManager como un Bean para que pueda ser utilizado en otros lugares (ej. en el AuthController)
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean // Define el encriptador de contraseñas
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean // Configura la cadena de filtros de seguridad
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Deshabilitar CSRF ya que usaremos JWT (stateless)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Configuración CORS
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler) // Manejador para cuando la autenticación falla
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No crear ni usar sesiones HTTP (API stateless)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll() // Permitir acceso a URLs públicas
                        .requestMatchers(HttpMethod.GET, "/api/v1/some-public-resource").permitAll() // Ejemplo de un GET público específico
                        .anyRequest().authenticated() // Cualquier otra petición requiere autenticación
                );

        // Añadir nuestro filtro JWT antes del filtro estándar de autenticación por usuario y contraseña
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean // Configuración de CORS
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200")); // Orígenes permitidos (URL de tu frontend Angular)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(List.of("x-auth-token"));
        configuration.setAllowCredentials(true); // Permitir credenciales (cookies, encabezados de autorización)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplicar esta configuración a todas las rutas
        return source;
    }
}