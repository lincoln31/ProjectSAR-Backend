package com.conjuntoresidencial.api.application.payment.service;

import com.conjuntoresidencial.api.domain.payment.model.Payment;
import com.conjuntoresidencial.api.domain.payment.model.PaymentStatus;
import com.conjuntoresidencial.api.domain.payment.port.in.ManagePaymentUseCase;
import com.conjuntoresidencial.api.domain.payment.port.out.PaymentRepositoryPort;
import com.conjuntoresidencial.api.domain.property.model.Apartment;
import com.conjuntoresidencial.api.domain.property.port.out.ApartmentRepositoryPort;
import com.conjuntoresidencial.api.domain.user.model.User;
import com.conjuntoresidencial.api.domain.user.port.out.UserRepositoryPort;
import com.conjuntoresidencial.api.domain.shared.exception.ResourceNotFoundException;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.PaymentRequestDto;
import com.conjuntoresidencial.api.infrastructure.web.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
// import java.time.LocalDate; // Si implementas getPaymentsByDateRange

@Service
@RequiredArgsConstructor
public class PaymentManagementService implements ManagePaymentUseCase {
    private final PaymentRepositoryPort paymentRepository;
    private final UserRepositoryPort userRepository;
    private final ApartmentRepositoryPort apartmentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public Payment recordPayment(PaymentRequestDto paymentDto, String authenticatedUsername) {
        User registeredByAdmin = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found: " + authenticatedUsername));

        Apartment apartment = apartmentRepository.findById(paymentDto.getApartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Apartment not found with id: " + paymentDto.getApartmentId()));

        // --- INICIALIZACIÓN PARA EL MAPPER ---
        // Forzar la carga de la torre asociada al apartamento si existe y si es LAZY
        // y si el mapper la necesita (que sí la necesita para towerName)
        if (apartment.getTower() != null) {
            // Simplemente acceder a un campo fuerza la inicialización del proxy
            apartment.getTower().getName();
        }
        // --- FIN DE INICIALIZACIÓN ---


        User payingUser = null;
        if (paymentDto.getUserId() != null) {
            payingUser = userRepository.findById(paymentDto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Paying user not found with id: " + paymentDto.getUserId()));

            // --- INICIALIZACIÓN PARA EL MAPPER (si el mapper usa payingUser.userProfile) ---
            if (payingUser.getUserProfile() != null) {
                payingUser.getUserProfile().getFirstName(); // O cualquier campo que uses
            }
            // --- FIN DE INICIALIZACIÓN ---
        }

        // --- INICIALIZACIÓN PARA EL MAPPER (para registeredByAdmin.userProfile) ---
        if (registeredByAdmin.getUserProfile() != null) {
            registeredByAdmin.getUserProfile().getFirstName(); // O cualquier campo que uses
        }
        // --- FIN DE INICIALIZACIÓN ---


        Payment payment = paymentMapper.toDomain(paymentDto);
        payment.setApartment(apartment);
        payment.setUser(payingUser);
        payment.setRegisteredBy(registeredByAdmin);

        Payment savedPayment = paymentRepository.save(payment);

        // --- ASEGURAR QUE LAS RELACIONES EN savedPayment ESTÉN CARGADAS ANTES DE DEVOLVER ---
        // Aunque ya las asignamos, Hibernate podría devolver proxies.
        // Re-acceder después de guardar asegura que estén cargadas si el mapper las necesita directamente del 'savedPayment'.
        // Sin embargo, el mapper probablemente usará las instancias 'apartment', 'payingUser', 'registeredByAdmin'
        // que ya inicializamos arriba. Si el mapper accede a savedPayment.getApartment().getTower().getName(),
        // entonces necesitamos asegurar que `savedPayment.getApartment().getTower()` esté inicializado.
        // La forma más segura es que el servicio devuelva el objeto `savedPayment` y el controlador
        // llame al mapper. El servicio YA ha inicializado las entidades que el mapper usará.

        // El problema suele ser cuando el mapper se llama FUERA de la transacción
        // (ej. en el controlador DESPUÉS de que el servicio retorna).
        // En tu caso, el mapper se llama en el controlador:
        // return ResponseEntity.status(HttpStatus.CREATED)
        //        .body(paymentMapper.toDto(paymentService.recordPayment(paymentDto, adminUsername)));
        // Esto significa que el paymentService.recordPayment DEBE devolver el objeto Payment
        // con todas las asociaciones necesarias ya cargadas o el mapper fallará.

        // Por lo tanto, la inicialización que hicimos arriba para `apartment.getTower().getName()`,
        // `payingUser.getUserProfile().getFirstName()`, y `registeredByAdmin.getUserProfile().getFirstName()`
        // es la clave. El objeto `savedPayment` que se devuelve tendrá estas relaciones cargadas
        // porque las entidades `apartment`, `payingUser`, y `registeredByAdmin` fueron "tocadas"
        // dentro de la transacción.

        return savedPayment;
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

        // Forzar carga para el mapper
        if (payment.getUser() != null) {
            // Acceder a cualquier campo del UserProfile para forzar su carga si es LAZY
            // y si userProfile es LAZY en la entidad User.
            // Si UserProfile es EAGER en User, esto no es estrictamente necesario, pero no hace daño.
            if (payment.getUser().getUserProfile() != null) {
                payment.getUser().getUserProfile().getFirstName(); // O cualquier otro getter
            } else {
                // Opcional: si el UserProfile PUEDE no existir y quieres manejarlo,
                // o loggear si esperas que siempre exista.
            }
        }
        if (payment.getApartment() != null) {
            payment.getApartment().getNumber();
            if (payment.getApartment().getTower() != null) {
                payment.getApartment().getTower().getName();
            }
        }
        if (payment.getRegisteredBy() != null) {
            if (payment.getRegisteredBy().getUserProfile() != null) {
                payment.getRegisteredBy().getUserProfile().getFirstName();
            }
        }
        return payment;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        // Forzar carga para el mapper
        payments.forEach(p -> {
            if (p.getUser() != null) p.getUser().getUsername();
            if (p.getApartment() != null) {
                p.getApartment().getNumber();
                if (p.getApartment().getTower() != null) p.getApartment().getTower().getName();
            }
            if (p.getRegisteredBy() != null) p.getRegisteredBy().getUsername();
        });
        return payments;
    }

    // Implementar getPaymentsByUserId y getPaymentsByApartmentId de forma similar, forzando carga si es necesario para el DTO

    @Override
    @Transactional
    public Payment updatePaymentStatus(Long paymentId, PaymentStatus newStatus, String authenticatedUsername) {
        Payment payment = getPaymentById(paymentId); // Reusa el método para obtener y lanzar NotFound
        // User adminUser = userRepository.findByUsername(authenticatedUsername)... // Podrías loggear quién cambió el estado

        payment.setStatus(newStatus);
        // Aquí podrías añadir lógica adicional si el estado cambia a PAGADO, VENCIDO, etc.
        // Ej: si es PAGADO y antes era PENDIENTE, enviar notificación.
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public void deletePayment(Long id) {
        if (!paymentRepository.findById(id).isPresent()){
            throw new ResourceNotFoundException("Payment not found with id: " + id);
        }
        paymentRepository.deleteById(id);
    }

    // TODO: Implementar los otros métodos de ManagePaymentUseCase
    @Override public List<Payment> getPaymentsByUserId(Long userId) { /* ... forzar carga y retornar ... */ return List.of(); }
    @Override public List<Payment> getPaymentsByApartmentId(Long apartmentId) { /* ... forzar carga y retornar ... */ return List.of(); }

}