package com.conjuntoresidencial.api.application.user.service;

import com.conjuntoresidencial.api.domain.user.model.Role;
import com.conjuntoresidencial.api.domain.user.model.User;
import com.conjuntoresidencial.api.domain.user.port.in.UserLoginUseCase;
import com.conjuntoresidencial.api.domain.user.port.in.UserRegistrationUseCase;
import com.conjuntoresidencial.api.domain.user.port.out.RoleRepositoryPort;
import com.conjuntoresidencial.api.domain.user.port.out.UserRepositoryPort;
import com.conjuntoresidencial.api.infrastructure.security.jwt.JwtTokenProvider;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.LoginRequestDTO;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.RegisterRequestDTO;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.AuthResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante para la consistencia

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthApplicationService implements UserRegistrationUseCase, UserLoginUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Override
    @Transactional // Asegura que la operación sea atómica
    public User registerUser(RegisterRequestDTO registerRequest) {
        if (userRepositoryPort.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Error: El nombre de usuario ya está en uso.");
        }
        if (userRepositoryPort.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Error: El email ya está en uso.");
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .phone(registerRequest.getPhone())
                .enabled(true) // Por defecto habilitado
                .build();

        Set<Role> roles = new HashSet<>();
        if (registerRequest.getRoles() == null || registerRequest.getRoles().isEmpty()) {
            // Asignar rol por defecto si no se especifican roles
            Role userRole = roleRepositoryPort.findByName("ROLE_RESIDENTE") // O el rol por defecto que definas
                    .orElseThrow(() -> new RuntimeException("Error: Rol por defecto no encontrado."));
            roles.add(userRole);
        } else {
            registerRequest.getRoles().forEach(roleName -> {
                // Asegúrate de que los nombres de rol en el DTO no tengan "ROLE_" si tus roles en BD sí lo tienen
                // o ajusta la lógica aquí.
                String effectiveRoleName = roleName.toUpperCase().startsWith("ROLE_") ? roleName.toUpperCase() : "ROLE_" + roleName.toUpperCase();
                Role role = roleRepositoryPort.findByName(effectiveRoleName)
                        .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado: " + effectiveRoleName));
                roles.add(role);
            });
        }
        user.setRoles(roles);
        return userRepositoryPort.save(user);
    }

    @Override
    public AuthResponseDTO loginUser(LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        // Asumimos que el principal es UserDetails de Spring Security
        // Si usaste una implementación personalizada, ajusta esto.
        org.springframework.security.core.userdetails.UserDetails userDetails =
                (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();

        // Necesitamos obtener el User de nuestro dominio para el ID y email,
        // ya que UserDetails de Spring no los tiene por defecto.
        User domainUser = userRepositoryPort.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Error: Usuario no encontrado después de la autenticación."));


        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new AuthResponseDTO(
                jwt,
                domainUser.getId(),
                userDetails.getUsername(),
                domainUser.getEmail(), // Obtenido de nuestro User de dominio
                roles
        );
    }
}