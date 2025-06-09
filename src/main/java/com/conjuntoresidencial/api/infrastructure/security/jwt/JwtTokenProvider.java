package com.conjuntoresidencial.api.infrastructure.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException; // Específico para errores de firma
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt.secret}") // Clave secreta para firmar el token, debe ser suficientemente larga y segura
    private String jwtSecretString;

    @Value("${app.jwt.expiration-ms}") // Tiempo de expiración del token en milisegundos
    private int jwtExpirationMs;

    @Value("${app.jwt.refresh-token.expiration-ms}") // Tiempo de expiración para el token de refresco (si lo implementas)
    private int jwtRefreshExpirationMs; // No lo usaremos activamente en esta primera etapa, pero es bueno tenerlo

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretString));
    }

    // Genera un token JWT para un usuario autenticado
    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return generateTokenFromUsername(userPrincipal.getUsername(), userPrincipal.getAuthorities());
    }

    // Genera un token JWT directamente desde el username y sus authorities (roles)
    public String generateTokenFromUsername(String username,  java.util.Collection<? extends GrantedAuthority> authorities) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles) // Añadimos los roles como un claim personalizado
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key(), Jwts.SIG.HS512) // Usamos el algoritmo HS512
                .compact();
    }

    // (Opcional) Genera un token de refresco
    public String generateRefreshToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtRefreshExpirationMs);

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key(), Jwts.SIG.HS512)
                .compact();
    }


    // Extrae el nombre de usuario del token JWT
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key()) // Verifica la firma usando la clave
                .build()
                .parseSignedClaims(token) // Parsea el token firmado
                .getPayload(); // Obtiene el cuerpo (claims) del token

        return claims.getSubject(); // El subject es el nombre de usuario
    }

    // Valida el token JWT
    public boolean validateToken(String authToken) {
        logger.debug("JwtTokenProvider: Iniciando validación del token..."); // LOG INICIO VALIDACIÓN
        if (authToken == null || authToken.isBlank()) {
            logger.warn("JwtTokenProvider: Token JWT está vacío o es nulo. Validación fallida.");
            return false;
        }
        try {
            Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(authToken);
            logger.info("JwtTokenProvider: Token JWT validado exitosamente."); // LOG VALIDACIÓN EXITOSA
            return true;
        } catch (SignatureException ex) {
            logger.error("JwtTokenProvider: Firma JWT inválida. Mensaje: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("JwtTokenProvider: Token JWT malformado. Mensaje: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("JwtTokenProvider: Token JWT expirado. Mensaje: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("JwtTokenProvider: Token JWT no soportado. Mensaje: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JwtTokenProvider: Las claims del JWT están vacías o son inválidas. Mensaje: {}", ex.getMessage());
        } catch (Exception ex) { // Captura genérica por si algo más falla
            logger.error("JwtTokenProvider: Ocurrió una excepción inesperada durante la validación del token. Mensaje: {}", ex.getMessage(), ex);
        }
        logger.warn("JwtTokenProvider: Validación del token JWT fallida (alguna excepción ocurrió)."); // LOG FALLO GENERAL
        return false;
    }
}