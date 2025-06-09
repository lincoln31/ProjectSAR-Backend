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
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(exception -> exception
                                .authenticationEntryPoint(unauthorizedHandler) // Manejador para 401
                        // .accessDeniedHandler(accessDeniedHandler()) // Opcional: para 403
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll() // Primero las reglas más específicas de permitir
                        // .requestMatchers(HttpMethod.GET, "/api/v1/some-public-resource").permitAll() // Otros ejemplos
                        .anyRequest().authenticated() // Luego, todo lo demás requiere autenticación
                );

        // Añade tu filtro JWT. Se ejecutará para cada request.
        // Su lógica interna debe ser la que determine si autentica o no,
        // pero no debe bloquear el paso si no hay token para rutas públicas.
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean // Configuración de CORS
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Es más seguro ser explícito con los orígenes en producción. Para desarrollo localhost:4200 está bien.
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // Considera ser más específico con los headers si es posible
        configuration.setAllowedHeaders(Arrays.asList("*")); // O especificar: "Authorization", "Content-Type", "X-Auth-Token", etc.
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Auth-Token")); // O los que necesites exponer
        configuration.setAllowCredentials(true);
        // configuration.setMaxAge(3600L); // Opcional: cuánto tiempo el resultado de una pre-flight request puede ser cacheado

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}