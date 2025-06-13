package com.conjuntoresidencial.api.domain.payment.port.in;

import com.conjuntoresidencial.api.domain.payment.model.Payment;
import com.conjuntoresidencial.api.domain.payment.model.PaymentStatus;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.PaymentRequestDto;
import org.springframework.data.domain.Page; // Correcta importación para Page
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails; // Importar UserDetails

import java.util.List;

// Ya no necesitamos List aquí para los métodos que ahora usan Page
// import java.util.List;
// import java.time.LocalDate; // Si se implementa getPaymentsByDateRange

public interface ManagePaymentUseCase {

    Payment recordPayment(PaymentRequestDto paymentDto, String authenticatedUsername);

    Payment getPaymentById(Long id, UserDetails authenticatedUserDetails); // Modificado: Añadir UserDetails

    Page<Payment> getAllPayments(Pageable pageable, UserDetails authenticatedUserDetails); // Modificado: Devuelve Page y recibe Pageable y UserDetails

    // Para obtener pagos de un usuario específico con paginación (usado por /me y potencialmente por admin)
    Page<Payment> getPaymentsByUserIdPaginated(Long userId, Pageable pageable, UserDetails authenticatedUserDetails);

    // Para obtener pagos de un apartamento específico con paginación (usado por admin)
    Page<Payment> getPaymentsByApartmentIdPaginated(Long apartmentId, Pageable pageable, UserDetails authenticatedUserDetails);

    // Los siguientes métodos que devuelven List<Payment> podrían mantenerse si hay casos de uso
    // internos que los necesiten sin paginación, pero para la API es mejor usar Pageable.
    // Si solo son para la API, considera eliminarlos o también hacerlos paginados y con UserDetails.
    // Por ahora, los actualizo para que también reciban UserDetails por consistencia si se mantienen.
    List<Payment> getPaymentsByUserId(Long userId, UserDetails authenticatedUserDetails);
    List<Payment> getPaymentsByApartmentId(Long apartmentId, UserDetails authenticatedUserDetails);

    // List<Payment> getPaymentsByDateRange(LocalDate start, LocalDate end, UserDetails authenticatedUserDetails); // Si se implementa

    Payment updatePaymentStatus(Long paymentId, PaymentStatus newStatus, String authenticatedUsername);
    // El authenticatedUsername aquí es para saber quién registró el cambio,
    // la verificación de si este usuario PUEDE hacer el cambio ya la hizo el @PreAuthorize en el controlador.

    void deletePayment(Long id); // La verificación de permisos se hace en el controlador.
}
