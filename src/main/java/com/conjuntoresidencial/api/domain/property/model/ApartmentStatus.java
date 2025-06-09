package com.conjuntoresidencial.api.domain.property.model;

public enum ApartmentStatus {
    DISPONIBLE,          // El apartamento está libre para ser ocupado o vendido
    OCUPADO_PROPIETARIO, // Ocupado por el propietario
    OCUPADO_INQUILINO,   // Ocupado por un inquilino
    EN_MANTENIMIENTO,    // No disponible debido a mantenimiento
    NO_DISPONIBLE,       // Otro estado de no disponibilidad general
    RESERVADO            // Reservado para una futura ocupación o venta
}
