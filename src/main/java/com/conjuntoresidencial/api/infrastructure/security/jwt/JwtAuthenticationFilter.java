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

        logger.info("JwtAuthenticationFilter: INICIO - Procesando request para URI: {}", request.getRequestURI()); // LOG INICIO

        try {
            String jwt = getJwtFromRequest(request);
            // LOG MODIFICADO para mostrar más claramente si el token está o no
            if (jwt == null) {
                logger.debug("JwtAuthenticationFilter: JWT extraído: Ausente");
            } else {
                logger.debug("JwtAuthenticationFilter: JWT extraído: Presente (longitud: {})", jwt.length());
                // Puedes loggear el token completo si estás en un entorno de desarrollo seguro, pero ten cuidado con información sensible.
                // logger.trace("JwtAuthenticationFilter: Token completo: {}", jwt); // Usa TRACE para logs muy detallados
            }


            if (StringUtils.hasText(jwt)) {
                logger.debug("JwtAuthenticationFilter: JWT tiene texto, intentando validar...");
                boolean isTokenValid = tokenProvider.validateToken(jwt); // Llama a validateToken
                logger.debug("JwtAuthenticationFilter: Resultado de tokenProvider.validateToken(jwt): {}", isTokenValid); // LOG RESULTADO VALIDACIÓN

                if (isTokenValid) {
                    String username = tokenProvider.getUsernameFromJWT(jwt);
                    logger.debug("JwtAuthenticationFilter: Username obtenido del token: {}", username); // LOG USERNAME

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    logger.debug("JwtAuthenticationFilter: UserDetails cargados para el username: {}", userDetails.getUsername()); // LOG USERDETAILS

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("JwtAuthenticationFilter: Usuario '{}' autenticado y contexto de seguridad establecido.", username); // LOG AUTENTICACIÓN EXITOSA
                } else {
                    logger.warn("JwtAuthenticationFilter: Validacion de token fallida (validateToken devolvió false) para URI: {}", request.getRequestURI());
                    // No se establece la autenticación si el token no es válido
                }
            } else {
                logger.debug("JwtAuthenticationFilter: No se encontró token JWT con texto en el request para URI: {}", request.getRequestURI());
            }
        } catch (Exception ex) {
            // Loguea la excepción completa para entender qué pudo haber fallado
            logger.error("JwtAuthenticationFilter: Excepción durante doFilterInternal para URI: {}. Mensaje: {}", request.getRequestURI(), ex.getMessage(), ex);
        }

        logger.info("JwtAuthenticationFilter: FIN - Continuando cadena de filtros para URI: {}", request.getRequestURI()); // LOG FIN
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            String token = bearerToken.substring(TOKEN_PREFIX.length());
            return token.trim(); // <--- AÑADIR .trim()
                    }
        logger.trace("JwtAuthenticationFilter: No se encontró el header '{}' o no comienza con '{}'", HEADER_AUTHORIZATION, TOKEN_PREFIX);
        return null;
    }
}