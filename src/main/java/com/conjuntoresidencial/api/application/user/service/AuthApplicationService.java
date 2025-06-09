package com.conjuntoresidencial.api.application.user.service;

import com.conjuntoresidencial.api.domain.shared.exception.ResourceNotFoundException;
import com.conjuntoresidencial.api.domain.user.model.Role;
import com.conjuntoresidencial.api.domain.user.model.User;
import com.conjuntoresidencial.api.domain.user.model.UserProfile;
import com.conjuntoresidencial.api.domain.user.port.in.UserLoginUseCase;
import com.conjuntoresidencial.api.domain.user.port.in.UserRegistrationUseCase;
import com.conjuntoresidencial.api.domain.user.port.out.RoleRepositoryPort;
import com.conjuntoresidencial.api.domain.user.port.out.UserProfileRepositoryPort;
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
import org.springframework.security.core.userdetails.UserDetails;
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

    private final UserRepositoryPort userRepository; // Nombre consistente
    private final UserProfileRepositoryPort userProfileRepository; // Necesario para el registro
    private final RoleRepositoryPort roleRepository; // Nombre consistente
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager; // Para login
    private final JwtTokenProvider tokenProvider; // Para login

    @Override
    @Transactional // Asegura que la operación sea atómica
    public User registerUser(RegisterRequestDTO registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Error: El nombre de usuario ya está en uso!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Error: El email ya está en uso!");
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .enabled(true)
                .build();

        Set<Role> userRoles = new HashSet<>();
        if (registerRequest.getRoles() == null || registerRequest.getRoles().isEmpty()) {
            Role defaultRole = roleRepository.findByName("ROLE_RESIDENTE") // Usar nombre consistente del campo
                    .orElseThrow(() -> new ResourceNotFoundException("Error: Rol por defecto no encontrado."));
            userRoles.add(defaultRole);
        } else {
            userRoles = registerRequest.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName.toUpperCase()) // Usar nombre consistente
                            .orElseThrow(() -> new ResourceNotFoundException("Error: Rol no encontrado: " + roleName)))
                    .collect(Collectors.toSet());
        }
        user.setRoles(userRoles);

        User savedUser = userRepository.save(user); // CORRECTO: Usar el userRepository inyectado

        UserProfile userProfile = UserProfile.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .phoneNumber(registerRequest.getPhone())
                .user(savedUser)
                .build();

        userProfileRepository.save(userProfile); // CORRECTO: Usar el userProfileRepository inyectado

        return savedUser;
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
        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        // Necesitamos obtener el User de nuestro dominio para el ID y email,
        // ya que UserDetails de Spring no los tiene por defecto.
        User domainUser = userRepository.findByUsername(userDetails.getUsername())
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