package com.conjuntoresidencial.api.infrastructure.persistence.postgresql.repository;


import com.conjuntoresidencial.api.domain.user.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Opcional si la clase base ya lo tiene, pero bueno para claridad
public interface RoleJpaRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
    // JpaRepository ya provee: save, findById, findAll, deleteById, etc.
}