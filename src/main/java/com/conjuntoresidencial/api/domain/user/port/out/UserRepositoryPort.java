package com.conjuntoresidencial.api.domain.user.port.out;


import com.conjuntoresidencial.api.domain.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findAll(); // Para listar usuarios (ej. por un admin)

    void deleteById(Long id);

    // Otros métodos podrían ser:
    // List<User> findByRole(Role role);
    // Page<User> findAll(Pageable pageable); // Para paginación
}