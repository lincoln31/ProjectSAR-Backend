package com.conjuntoresidencial.api.infrastructure.config;

import com.conjuntoresidencial.api.domain.user.model.Role;
import com.conjuntoresidencial.api.domain.user.port.out.RoleRepositoryPort; // Importa tu puerto
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component // Para que Spring lo detecte y gestione
@RequiredArgsConstructor // Lombok para inyección de dependencias
public class DataInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepositoryPort roleRepositoryPort; // Inyectamos nuestro puerto del dominio

    // Define aquí los nombres de los roles que quieres crear
    // Es una buena práctica usar un prefijo como "ROLE_" para los nombres de roles,
    // ya que Spring Security a menudo lo espera por defecto en algunas configuraciones.
    private static final List<String> INITIAL_ROLES = Arrays.asList(
            "ROLE_ADMIN",
            "ROLE_RESIDENTE",
            "ROLE_PROPIETARIO",
            "ROLE_INQUILINO"
            // Añade más roles si los necesitas
    );

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Iniciando la carga de datos iniciales...");

        INITIAL_ROLES.forEach(roleName -> {
            if (roleRepositoryPort.findByName(roleName).isEmpty()) {
                Role newRole = Role.builder().name(roleName).build();
                roleRepositoryPort.save(newRole);
                logger.info("Rol '{}' creado exitosamente.", roleName);
            } else {
                logger.info("Rol '{}' ya existe, no se creará.", roleName);
            }
        });

        logger.info("Carga de datos iniciales completada.");

        // Aquí también podrías crear un usuario administrador por defecto si lo deseas,
        // pero asegúrate de manejar las contraseñas de forma segura (ej. desde configuración).
        // Por ejemplo:
        /*
        if (userRepositoryPort.findByUsername("admin").isEmpty()) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            Set<Role> adminRoles = new HashSet<>();
            roleRepositoryPort.findByName("ROLE_ADMIN").ifPresent(adminRoles::add);

            User adminUser = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123")) // CAMBIAR ESTO EN PRODUCCIÓN
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .enabled(true)
                .roles(adminRoles)
                .build();
            userRepositoryPort.save(adminUser);
            logger.info("Usuario administrador por defecto creado.");
        }
        */
    }
}