package com.conjuntoresidencial.api.domain.user.port.in;

import com.conjuntoresidencial.api.domain.user.model.User;
import com.conjuntoresidencial.api.domain.user.model.UserProfile;
// Importar DTOs si se definen para los casos de uso
import com.conjuntoresidencial.api.infrastructure.web.dto.request.RegisterRequestDTO;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.UserProfileRequestDto;


public interface UserProfileManagementUseCase {

    UserProfile createProfile(Long userId, UserProfileRequestDto profileDetails); // O pasar UserProfile directamente
    UserProfile getProfileByUserId(Long userId);
    UserProfile updateProfileByUserId(Long userId, UserProfileRequestDto profileDetails);
    // Opcional: UserProfile getProfileByUsername(String username);
    // Opcional: void deleteProfileByUserId(Long userId); (considerar si esto elimina al usuario o solo el perfil)
}