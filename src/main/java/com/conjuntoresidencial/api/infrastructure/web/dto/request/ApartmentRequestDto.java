package com.conjuntoresidencial.api.infrastructure.web.dto.request;
import com.conjuntoresidencial.api.domain.property.model.ApartmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApartmentRequestDto {
    @NotBlank(message = "Apartment number cannot be blank")
    @Size(max = 50)
    private String number;



    @Size(max = 255)
    private String description;

    @NotNull(message = "Tower ID cannot be null")
    private Long towerId; // ID de la torre a la que pertenece

    @NotNull(message = "Status cannot be null")
    private ApartmentStatus status;
}