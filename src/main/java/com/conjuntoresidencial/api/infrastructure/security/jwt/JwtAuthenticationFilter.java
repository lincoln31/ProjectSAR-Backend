package com.conjuntoresidencial.api.infrastructure.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // Importar UserDetailsService de Spring
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Marcamos como un componente de Spring para que sea detectado y utilizado
@RequiredArgsConstructor // Lombok para inyección de dependencias
public class JwtAuthenticationFilter extends OncePerRequestFilter { // Se ejecuta una vez por solicitud

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService; // Nuestra implementación UserDetailsServiceImpl

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromJWT(jwt);

                // Cargar UserDetails (incluye autoridades/roles)
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Crear el objeto de autenticación
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, // El principal (nuestro UserDetails)
                        null,        // Credenciales (no necesarias aquí ya que el token ya está validado)
                        userDetails.getAuthorities() // Las autoridades (roles)
                );

                // Establecer detalles adicionales de la autenticación web
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establecer la autenticación en el contexto de seguridad de Spring
                // A partir de este punto, Spring Security considera al usuario como autenticado para esta solicitud
                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.debug("Usuario '{}' autenticado exitosamente con token JWT.", username);
            }
        } catch (Exception ex) {
            logger.error("No se pudo establecer la autenticación del usuario en el contexto de seguridad", ex);
            // No es necesario lanzar la excepción aquí, ya que si SecurityContextHolder no tiene autenticación,
            // los siguientes filtros o la configuración de autorización lo manejarán (ej. JwtAuthenticationEntryPoint).
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    // Extrae el token JWT del encabezado "Authorization"
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length()); // Remueve el prefijo "Bearer "
        }
        return null;
    }
}