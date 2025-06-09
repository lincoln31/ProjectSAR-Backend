package com.conjuntoresidencial.api.application.user.service;

import com.conjuntoresidencial.api.domain.user.model.Role;
import com.conjuntoresidencial.api.domain.user.model.User;
import com.conjuntoresidencial.api.domain.user.model.UserProfile;
import com.conjuntoresidencial.api.domain.user.port.in.UserProfileManagementUseCase;
import com.conjuntoresidencial.api.domain.user.port.out.RoleRepositoryPort;
import com.conjuntoresidencial.api.domain.user.port.out.UserProfileRepositoryPort;
import com.conjuntoresidencial.api.domain.user.port.out.UserRepositoryPort; // Para obtener el User
import com.conjuntoresidencial.api.infrastructure.web.dto.request.RegisterRequestDTO;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.UserProfileRequestDto;
import com.conjuntoresidencial.api.domain.shared.exception.ResourceNotFoundException; // Crear esta excepción
import com.conjuntoresidencial.api.domain.user.port.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserProfileManagementService implements UserProfileManagementUseCase {

    private final UserProfileRepositoryPort userProfileRepository;

    private final UserRepositoryPort userRepository;




    @Override
    @Transactional
    public UserProfile createProfile(Long userId, UserProfileRequestDto profileDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Verificar si ya existe un perfil para este usuario
        if (userProfileRepository.findByUser(user).isPresent()) {
            // Lanzar una excepción apropiada, ej: ProfileAlreadyExistsException
            throw new IllegalStateException("Profile already exists for user: " + userId);
        }

        UserProfile userProfile = UserProfile.builder()
                .firstName(profileDetails.getFirstName())
                .lastName(profileDetails.getLastName())
                .phoneNumber(profileDetails.getPhoneNumber())
                .documentId(profileDetails.getDocumentId())
                .address(profileDetails.getAddress())
                .birthDate(profileDetails.getBirthDate())
                .user(user)
                .build();
        return userProfileRepository.save(userProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfile getProfileByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return userProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + userId));
    }

    @Override
    @Transactional
    public UserProfile updateProfileByUserId(Long userId, UserProfileRequestDto profileDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        UserProfile userProfile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + userId));

        userProfile.setFirstName(profileDetails.getFirstName());
        userProfile.setLastName(profileDetails.getLastName());
        userProfile.setPhoneNumber(profileDetails.getPhoneNumber());
        userProfile.setDocumentId(profileDetails.getDocumentId());
        userProfile.setAddress(profileDetails.getAddress());
        userProfile.setBirthDate(profileDetails.getBirthDate());

        return userProfileRepository.save(userProfile);
    }
}