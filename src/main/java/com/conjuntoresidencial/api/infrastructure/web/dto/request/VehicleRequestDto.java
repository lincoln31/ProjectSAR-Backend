package com.conjuntoresidencial.api.infrastructure.web.dto.request;

import com.conjuntoresidencial.api.domain.vehicle.model.VehicleStatus;
import com.conjuntoresidencial.api.domain.vehicle.model.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VehicleRequestDto {


    @NotBlank(message = "License plate cannot be blank")
    @Size(max = 20, message = "License plate must be less than 20 characters")
    private String licensePlate;

    // Asumiendo que VehicleType es tu Enum: AUTOMOVIL, MOTOCICLETA, etc.
    @NotNull(message = "Vehicle type cannot be null")
    private VehicleType type; // Si envías "AUTOMOVIL", etc.

    @Size(max = 100)
    private String brand; // Marca (Ej: Mazda, Yamaha)

    @Size(max = 100)
    private String model; // Modelo (Ej: CX-5, NMAX)

    @Size(max = 50)
    private String color;

    // CAMBIO AQUÍ: Usar el documento del residente
    @NotBlank(message = "Resident document ID cannot be blank")
    @Size(max = 50) // Ajusta el tamaño según sea necesario
    private String residentDocumentId;

}