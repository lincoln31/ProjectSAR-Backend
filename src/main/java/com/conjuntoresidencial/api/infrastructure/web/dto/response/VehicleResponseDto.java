package com.conjuntoresidencial.api.infrastructure.web.dto.response;

import com.conjuntoresidencial.api.domain.vehicle.model.VehicleStatus;
import com.conjuntoresidencial.api.domain.vehicle.model.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VehicleResponseDto {
    private Long id;
    private String plate;
    private VehicleType type;
    private String brand;
    private String model;
    private String color;
    private VehicleStatus status;

    // Datos del propietario
    private Long ownerUserId;
    private String ownerFullName; // Nombre y apellido del UserProfile
    private String ownerDocumentId; // Cédula del UserProfile

    // Datos de ubicación (derivados de la residencia principal del owner, puede ser complejo)
    // Por ahora, mantenlo simple o considera cómo obtener esta info de forma eficiente.
    // Si el vehículo está directamente asociado a un apartamento, sería más fácil.
    private String towerName;
    private String apartmentNumber;
    @NotBlank(message = "License plate cannot be blank")
    @Size(max = 20, message = "License plate must be less than 20 characters")
    private String licensePlate;

    // Asumiendo que VehicleType es tu Enum: AUTOMOVIL, MOTOCICLETA, etc.



    // CAMBIO AQUÍ: Usar el documento del residente
    @NotBlank(message = "Resident document ID cannot be blank")
    @Size(max = 50) // Ajusta el tamaño según sea necesario
    private String residentDocumentId;

    // Opcional: Si un vehículo también puede estar asociado directamente a un apartamento
    // private Long apartmentId;
}