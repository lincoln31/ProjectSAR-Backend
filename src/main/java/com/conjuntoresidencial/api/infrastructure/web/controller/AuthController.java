package com.conjuntoresidencial.api.infrastructure.web.controller;

import com.conjuntoresidencial.api.domain.user.port.in.UserLoginUseCase;
import com.conjuntoresidencial.api.domain.user.port.in.UserRegistrationUseCase;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.LoginRequestDTO;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.RegisterRequestDTO;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.AuthResponseDTO;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.MessageResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth") // Ruta base para los endpoints de autenticación
@RequiredArgsConstructor
public class AuthController {

    private final UserRegistrationUseCase userRegistrationUseCase;
    private final UserLoginUseCase userLoginUseCase;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        try {
            userRegistrationUseCase.registerUser(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MessageResponseDTO("Usuario registrado exitosamente!"));
        } catch (IllegalArgumentException e) {
            // Esta excepción la lanzamos desde el servicio si el usuario/email ya existe
            return ResponseEntity.badRequest().body(new MessageResponseDTO(e.getMessage()));
        } catch (RuntimeException e) {
            // Captura otras excepciones, como rol no encontrado
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponseDTO("Error al registrar el usuario: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            AuthResponseDTO authResponse = userLoginUseCase.loginUser(loginRequest);
            return ResponseEntity.ok(authResponse);
        } catch (org.springframework.security.core.AuthenticationException e) {
            // Captura excepciones de autenticación de Spring Security (ej. BadCredentialsException)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponseDTO("Error de autenticación: Credenciales inválidas."));
        } catch (RuntimeException e) {
            // Otras excepciones inesperadas durante el login
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponseDTO("Error durante el inicio de sesión: " + e.getMessage()));
        }
    }
}