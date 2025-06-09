package com.conjuntoresidencial.api.infrastructure.web.dto.request;

import lombok.Data;
import java.time.LocalDate;
// import javax.validation.constraints.*; // Para Jakarta EE 8 (Spring Boot 2.x)
import jakarta.validation.constraints.*; // Para Jakarta EE 9+ (Spring Boot 3.x)

@Data
public class UserProfileRequestDto {
    @NotBlank(message = "First name cannot be blank")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 100)
    private String lastName;

    @Size(max = 20)
    private String phoneNumber;

    @Size(max = 50)
    private String documentId;

    private String address;
    private LocalDate birthDate; // Considerar formato de fecha en la API (ISO_DATE)
}