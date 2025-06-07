package com.conjuntoresidencial.api.domain.user.port.in;

import com.conjuntoresidencial.api.infrastructure.web.dto.request.LoginRequestDTO;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.AuthResponseDTO;

public interface UserLoginUseCase {
    AuthResponseDTO loginUser(LoginRequestDTO loginRequest);
}