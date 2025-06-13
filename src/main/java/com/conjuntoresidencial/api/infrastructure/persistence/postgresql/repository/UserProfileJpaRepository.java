package com.conjuntoresidencial.api.infrastructure.persistence.postgresql.repository;


import com.conjuntoresidencial.api.domain.user.model.User;
import com.conjuntoresidencial.api.domain.user.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserProfileJpaRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUser(User user);
    Optional<UserProfile> findByUserId(Long userId);
    Optional<UserProfile> findByDocumentId(String documentId); // NUEVO MÉTODO
}