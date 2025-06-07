package com.conjuntoresidencial.api.domain.user.port.in;

import com.conjuntoresidencial.api.domain.user.model.User; // Asume que el DTO se mapeará a User en el servicio
import com.conjuntoresidencial.api.infrastructure.web.dto.request.RegisterRequestDTO;

public interface UserRegistrationUseCase {
    User registerUser(RegisterRequestDTO registerRequest);
}