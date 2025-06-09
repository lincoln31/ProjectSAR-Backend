package com.conjuntoresidencial.api.infrastructure.web.controller;


import com.conjuntoresidencial.api.domain.user.port.in.UserProfileManagementUseCase;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.UserProfileRequestDto;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.UserProfileResponseDto;
import com.conjuntoresidencial.api.infrastructure.web.mapper.UserProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
// import javax.validation.Valid; // Spring Boot 2.x
import jakarta.validation.Valid; // Spring Boot 3.x

@RestController
@RequestMapping("/api/v1/users") // O /api/v1/profiles
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileManagementUseCase userProfileManagementUseCase;
    private final UserProfileMapper userProfileMapper;
    // Necesitarás el UserRepositoryPort del Módulo 1 para obtener el ID del usuario autenticado si no lo tienes ya
    private final com.conjuntoresidencial.api.domain.user.port.out.UserRepositoryPort userRepositoryPort;


    // Endpoint para que el usuario autenticado obtenga su propio perfil
    @GetMapping("/me/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileResponseDto> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        // Necesitamos obtener el ID del usuario a partir de userDetails.getUsername()
        // Asumiendo que UserRepositoryPort tiene un método findByUsername
        Long userId = userRepositoryPort.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new com.conjuntoresidencial.api.domain.shared.exception.ResourceNotFoundException("User not found"))
                .getId();

        com.conjuntoresidencial.api.domain.user.model.UserProfile profile = userProfileManagementUseCase.getProfileByUserId(userId);
        return ResponseEntity.ok(userProfileMapper.toDto(profile));
    }

    // Endpoint para que el usuario autenticado actualice su propio perfil
    @PutMapping("/me/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileResponseDto> updateMyProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                                  @Valid @RequestBody UserProfileRequestDto profileRequestDto) {
        Long userId = userRepositoryPort.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new com.conjuntoresidencial.api.domain.shared.exception.ResourceNotFoundException("User not found"))
                .getId();
        com.conjuntoresidencial.api.domain.user.model.UserProfile updatedProfile =
                userProfileManagementUseCase.updateProfileByUserId(userId, profileRequestDto);
        return ResponseEntity.ok(userProfileMapper.toDto(updatedProfile));
    }

    // Endpoint para que un administrador cree un perfil para un usuario específico
    // (Puede que la creación de perfil se haga al crear el usuario, ajustar según flujo)
    @PostMapping("/{userId}/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponseDto> createProfileForUser(@PathVariable Long userId,
                                                                       @Valid @RequestBody UserProfileRequestDto profileRequestDto) {
        com.conjuntoresidencial.api.domain.user.model.UserProfile createdProfile =
                userProfileManagementUseCase.createProfile(userId, profileRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userProfileMapper.toDto(createdProfile));
    }


    // Endpoint para que un administrador obtenga el perfil de cualquier usuario
    @GetMapping("/{userId}/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponseDto> getProfileByUserIdAsAdmin(@PathVariable Long userId) {
        com.conjuntoresidencial.api.domain.user.model.UserProfile profile = userProfileManagementUseCase.getProfileByUserId(userId);
        return ResponseEntity.ok(userProfileMapper.toDto(profile));
    }

    // Endpoint para que un administrador actualice el perfil de cualquier usuario
    @PutMapping("/{userId}/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponseDto> updateProfileByUserIdAsAdmin(@PathVariable Long userId,
                                                                               @Valid @RequestBody UserProfileRequestDto profileRequestDto) {
        com.conjuntoresidencial.api.domain.user.model.UserProfile updatedProfile =
                userProfileManagementUseCase.updateProfileByUserId(userId, profileRequestDto);
        return ResponseEntity.ok(userProfileMapper.toDto(updatedProfile));
    }
}
