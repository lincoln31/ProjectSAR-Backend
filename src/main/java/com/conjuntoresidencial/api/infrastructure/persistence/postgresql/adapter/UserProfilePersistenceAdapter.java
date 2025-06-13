package com.conjuntoresidencial.api.infrastructure.persistence.postgresql.adapter;

import com.conjuntoresidencial.api.domain.user.model.User;
import com.conjuntoresidencial.api.domain.user.model.UserProfile;
import com.conjuntoresidencial.api.domain.user.port.out.UserProfileRepositoryPort;
import com.conjuntoresidencial.api.infrastructure.persistence.postgresql.repository.UserProfileJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserProfilePersistenceAdapter implements UserProfileRepositoryPort {

    private final UserProfileJpaRepository userProfileJpaRepository;

    @Override
    public Optional<UserProfile> findByDocumentId(String documentId) {
        return userProfileJpaRepository.findByDocumentId(documentId); // ¡CORREGIDO!
    }

    @Override
    public UserProfile save(UserProfile userProfile) {
        return userProfileJpaRepository.save(userProfile);
    }

    @Override
    public Optional<UserProfile> findById(Long id) {
        return userProfileJpaRepository.findById(id);
    }

    @Override
    public Optional<UserProfile> findByUser(User user) {
        return userProfileJpaRepository.findByUser(user);
    }

    @Override
    public Optional<UserProfile> findByUserId(Long userId) {
        return userProfileJpaRepository.findByUserId(userId);
    }

    @Override
    public void delete(UserProfile userProfile) {
        userProfileJpaRepository.delete(userProfile);
    }
}