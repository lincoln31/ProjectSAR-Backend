package com.conjuntoresidencial.api.infrastructure.security.service;

import com.conjuntoresidencial.api.domain.user.model.Role;
import com.conjuntoresidencial.api.domain.user.model.User;
import com.conjuntoresidencial.api.domain.user.port.out.UserRepositoryPort; // Importa tu puerto
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service // Marcamos como un servicio de Spring
@RequiredArgsConstructor // Lombok para inyección de dependencias vía constructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepositoryPort userRepositoryPort; // Inyectamos nuestro puerto del dominio

    @Override
    @Transactional(readOnly = true) // Es una operación de solo lectura
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Permitimos iniciar sesión con nombre de usuario o email
        User user = userRepositoryPort.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepositoryPort.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username o email: " + usernameOrEmail)));

        if (!user.isEnabled()) {
            throw new UsernameNotFoundException("Usuario deshabilitado: " + usernameOrEmail);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                mapRolesToAuthorities(user.getRoles())
        );
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}