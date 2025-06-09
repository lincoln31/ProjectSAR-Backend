package com.conjuntoresidencial.api.domain.user.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
// import javax.persistence.*; // Para Jakarta EE 9+ (Spring Boot 3+)
import jakarta.persistence.*; // Para Jakarta EE 9+ (Spring Boot 3+)

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_profiles")
@ToString(exclude = {"user"})
@EqualsAndHashCode(exclude = {"user"})
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    @JsonBackReference("user-profile")
    private User user;
}