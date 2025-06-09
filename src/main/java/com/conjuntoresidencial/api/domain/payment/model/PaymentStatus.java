package com.conjuntoresidencial.api.domain.payment.model;

public enum PaymentStatus {
    PENDIENTE, // El pago está pendiente de ser realizado o confirmado
    PAGADO,    // El pago ha sido confirmado
    VENCIDO,   // El pago no se realizó a tiempo (lógica adicional podría ser necesaria para esto)
    CANCELADO, // El pago fue cancelado
    REEMBOLSADO // El pago fue reembolsado
    // Puedes añadir más estados según necesites
}