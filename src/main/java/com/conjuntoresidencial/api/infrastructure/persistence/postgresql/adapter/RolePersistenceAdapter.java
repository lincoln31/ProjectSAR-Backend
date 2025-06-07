package com.conjuntoresidencial.api.infrastructure.persistence.postgresql.adapter;
import com.conjuntoresidencial.api.domain.user.model.Role;
import com.conjuntoresidencial.api.domain.user.port.out.RoleRepositoryPort;
import com.conjuntoresidencial.api.infrastructure.persistence.postgresql.repository.RoleJpaRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component; // O @Service

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component // Marcamos como un componente de Spring para inyección de dependencias
@RequiredArgsConstructor // Lombok para inyectar dependencias vía constructor
public class RolePersistenceAdapter implements RoleRepositoryPort {



    private final RoleJpaRepository roleJpaRepository; // Inyección de dependencia


    @Override
    public Role save(Role role) {
        return roleJpaRepository.save(role);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleJpaRepository.findByName(name);
    }

    @Override
    public Set<Role> findAll() {
        return new HashSet<>(roleJpaRepository.findAll());
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleJpaRepository.findById(id);
    }
}