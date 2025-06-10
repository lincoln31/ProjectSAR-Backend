package com.conjuntoresidencial.api.domain.transaction.model;

public enum TransactionType {
    PAGO_RECIBIDO,      // Un pago ha sido confirmado
    REEMBOLSO,          // Se ha realizado un reembolso
    AJUSTE_CREDITO,     // Un crédito aplicado a una cuenta/apartamento
    AJUSTE_DEBITO,      // Un débito aplicado
    CARGO_ADICIONAL     // Un cargo nuevo no directamente de un pago regular
    // Puedes añadir más tipos según la necesidad
}