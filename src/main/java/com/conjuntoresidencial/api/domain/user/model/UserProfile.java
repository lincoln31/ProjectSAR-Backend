package com.conjuntoresidencial.api.domain.user.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
// import javax.persistence.*; // Para Jakarta EE 9+ (Spring Boot 3+)
import jakarta.persistence.*; // Para Jakarta EE 9+ (Spring Boot 3+)

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 50) // Podría ser único si es un identificador nacional
    private String documentId;

    private String address; // Puede ser más complejo (objeto Embebido) si es necesario

    private LocalDate birthDate;

    @OneToOne(fetch = FetchType.LAZY) // O EAGER si siempre lo necesitas con User
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user; // Relación con la entidad User
}