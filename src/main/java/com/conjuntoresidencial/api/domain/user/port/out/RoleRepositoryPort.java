package com.conjuntoresidencial.api.domain.user.port.out;

import com.conjuntoresidencial.api.domain.user.model.Role;

import java.util.Optional;
import java.util.Set;

public interface RoleRepositoryPort {

    Role save(Role role);

    Optional<Role> findByName(String name);

    Set<Role> findAll(); // Podríamos necesitar esto para asignar roles o listarlos

    Optional<Role> findById(Long id);

    // Podríamos añadir más métodos si fueran necesarios, como delete, etc.
}