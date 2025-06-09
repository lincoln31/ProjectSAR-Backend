package com.conjuntoresidencial.api.domain.user.port.out;

import com.conjuntoresidencial.api.domain.user.model.User;
import com.conjuntoresidencial.api.domain.user.model.UserProfile;
import java.util.Optional;

public interface UserProfileRepositoryPort {
    UserProfile save(UserProfile userProfile);
    Optional<UserProfile> findById(Long id);
    Optional<UserProfile> findByUser(User user); // Muy útil
    Optional<UserProfile> findByUserId(Long userId); // Alternativa
    void delete(UserProfile userProfile);
}