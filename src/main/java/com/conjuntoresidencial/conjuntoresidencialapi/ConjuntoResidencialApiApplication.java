package com.conjuntoresidencial.conjuntoresidencialapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan; // Asegúrate de importar esta
import org.springframework.context.annotation.ComponentScan;   // Asegúrate de importar esta
import org.springframework.data.jpa.repository.config.EnableJpaRepositories; // Asegúrate de importar esta

@SpringBootApplication
// Escanea todos los componentes (Servicios, Controladores, etc.)
// a partir del paquete 'com.conjuntoresidencial.api' y todos sus subpaquetes.
@ComponentScan(basePackages = "com.conjuntoresidencial.api")
// Escanea específicamente las interfaces JpaRepository en este paquete y sus subpaquetes.
@EnableJpaRepositories("com.conjuntoresidencial.api.infrastructure.persistence.postgresql.repository")
// Escanea específicamente las entidades (@Entity) en este paquete y sus subpaquetes.
@EntityScan("com.conjuntoresidencial.api.domain")
public class ConjuntoResidencialApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConjuntoResidencialApiApplication.class, args);
    }

}
