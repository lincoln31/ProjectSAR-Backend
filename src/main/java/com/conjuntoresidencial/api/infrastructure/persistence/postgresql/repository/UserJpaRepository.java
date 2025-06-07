package com.conjuntoresidencial.api.infrastructure.persistence.postgresql.repository;


import com.conjuntoresidencial.api.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
    // JpaRepository ya provee: save, findById, findAll, deleteById, etc.
}