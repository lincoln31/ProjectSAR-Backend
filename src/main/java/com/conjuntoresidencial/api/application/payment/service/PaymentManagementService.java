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
// El mapper no se usa directamente en este servicio si las entidades se devuelven al controlador
// import com.conjuntoresidencial.api.infrastructure.web.mapper.PaymentMapper;
import com.conjuntoresidencial.api.domain.transaction.model.Transaction;
import com.conjuntoresidencial.api.domain.transaction.model.TransactionType;
import com.conjuntoresidencial.api.domain.transaction.port.out.TransactionRepositoryPort;
// Importar ResidencyRepositoryPort si se implementa la lógica de isRelatedToApartmentOfUser
// import com.conjuntoresidencial.api.domain.residency.port.out.ResidencyRepositoryPort;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException; // Importar para el manejo de permisos
import org.springframework.security.access.method.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentManagementService implements ManagePaymentUseCase {

    private final PaymentRepositoryPort paymentRepository;
    private final UserRepositoryPort userRepository;
    private final ApartmentRepositoryPort apartmentRepository;
    // private final PaymentMapper paymentMapper; // No es necesario si el servicio devuelve entidades y el controlador mapea
    private final TransactionRepositoryPort transactionRepository;
    // private final ResidencyRepositoryPort residencyRepository; // Descomentar si se usa

    // Método de ayuda privado para inicializar campos LAZY
    private void initializeLazyFieldsForDto(Payment payment) {
        if (payment == null) return;

        if (payment.getUser() != null) {
            // Acceder al username siempre es seguro (no LAZY si es parte de User)
            payment.getUser().getUsername();
            if (payment.getUser().getUserProfile() != null) {
                payment.getUser().getUserProfile().getFirstName();
            }
        }
        if (payment.getApartment() != null) {
            payment.getApartment().getNumber();
            if (payment.getApartment().getTower() != null) {
                payment.getApartment().getTower().getName();
            }
        }
        if (payment.getRegisteredBy() != null) {
            payment.getRegisteredBy().getUsername();
            if (payment.getRegisteredBy().getUserProfile() != null) {
                payment.getRegisteredBy().getUserProfile().getFirstName();
            }
        }
    }

    @Override
    @Transactional
    public Payment recordPayment(PaymentRequestDto paymentDto, String authenticatedUsername) {
        User registeredByAdmin = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found: " + authenticatedUsername));
        initializeLazyFieldsForUser(registeredByAdmin); // Para el createdBy de la transacción

        Apartment apartment = apartmentRepository.findById(paymentDto.getApartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Apartment not found with id: " + paymentDto.getApartmentId()));
        initializeLazyFieldsForApartment(apartment); // Para el mapper y la transacción

        User payingUser = null;
        if (paymentDto.getUserId() != null) {
            payingUser = userRepository.findById(paymentDto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Paying user not found with id: " + paymentDto.getUserId()));
            initializeLazyFieldsForUser(payingUser); // Para el mapper y la transacción
        }

        // El PaymentMapper ya no se usa aquí para toDomain si el controlador lo hace
        // o si construimos el Payment manualmente. Asumimos construcción manual o un mapper simple.
        Payment payment = Payment.builder()
                .amount(paymentDto.getAmount())
                .concept(paymentDto.getConcept())
                .paymentDate(paymentDto.getPaymentDate())
                .dueDate(paymentDto.getDueDate())
                .status(paymentDto.getStatus()) // Asumimos que el DTO puede llevar un estado inicial
                .paymentMethod(paymentDto.getPaymentMethod())
                .referenceNumber(paymentDto.getReferenceNumber())
                .build();

        payment.setApartment(apartment);
        payment.setUser(payingUser); // Puede ser null
        payment.setRegisteredBy(registeredByAdmin);

        Payment savedPayment = paymentRepository.save(payment);

        // La inicialización de los campos de 'savedPayment' se hará antes de devolverlo si es necesario,
        // o el controlador llamará a un método getById que ya los inicializa.
        // Por ahora, devolvemos 'savedPayment'. Si el mapper se llama en el controlador,
        // el controlador deberá llamar a un método de servicio que inicialice (como getPaymentById).
        // Para recordPayment, si el controlador mapea el resultado directamente, debemos inicializar aquí.
        initializeLazyFieldsForDto(savedPayment);
        return savedPayment;
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentById(Long id, UserDetails authenticatedUserDetails) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

        boolean isAdmin = authenticatedUserDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            User currentUser = userRepository.findByUsername(authenticatedUserDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found: " + authenticatedUserDetails.getUsername()));

            boolean ownsPaymentDirectly = payment.getUser() != null && payment.getUser().getId().equals(currentUser.getId());

            // TODO: Implementar lógica para isRelatedToApartmentOfUser si es necesario
            // boolean isRelatedToApartmentOfUser = false;
            // if (payment.getApartment() != null && !ownsPaymentDirectly) {
            //     // isRelatedToApartmentOfUser = residencyRepository.isUserAssociatedWithApartment(currentUser.getId(), payment.getApartment().getId());
            // }

            if (!ownsPaymentDirectly /* && !isRelatedToApartmentOfUser */) {
                throw new AccessDeniedException("You do not have permission to view this payment.");
            }
        }
        initializeLazyFieldsForDto(payment);
        return payment;
    }

    // Método de ayuda para inicializar usuario (usado en recordPayment y updatePaymentStatus)
    private void initializeLazyFieldsForUser(User user) {
        if (user != null && user.getUserProfile() != null) {
            user.getUserProfile().getFirstName();
        }
    }
    // Método de ayuda para inicializar apartamento (usado en recordPayment)
    private void initializeLazyFieldsForApartment(Apartment apartment) {
        if (apartment != null && apartment.getTower() != null) {
            apartment.getTower().getName();
        }
    }


    @Override
    @Transactional(readOnly = true)
    public Page<Payment> getAllPayments(Pageable pageable, UserDetails authenticatedUserDetails) { // Ahora con UserDetails
        boolean isAdmin = authenticatedUserDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        Page<Payment> payments;
        if (isAdmin) {
            payments = (Page<Payment>) paymentRepository.findAll(pageable);
        } else {
            User currentUser = userRepository.findByUsername(authenticatedUserDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found: " + authenticatedUserDetails.getUsername()));
            payments = paymentRepository.findByUserId(currentUser.getId(), pageable); // Asume que este método existe
        }
        payments.forEach(this::initializeLazyFieldsForDto);
        return payments;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Payment> getPaymentsByApartmentIdPaginated(Long apartmentId, Pageable pageable, UserDetails authenticatedUserDetails) {
        // Primero, verificar si el apartamento existe
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Apartment not found with id: " + apartmentId));

        boolean isAdmin = authenticatedUserDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            User currentUser = userRepository.findByUsername(authenticatedUserDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found: " + authenticatedUserDetails.getUsername()));
            // Verificar si el usuario actual tiene permiso para ver pagos de este apartamento
            // (ej. si tiene una residencia en él)
            // boolean canView = residencyRepository.isUserAssociatedWithApartment(currentUser.getId(), apartmentId);
            // if (!canView) {
            //    throw new AccessDeniedException("You do not have permission to view payments for this apartment.");
            // }
            // Por ahora, si no es admin, podría no permitirse ver pagos por apartamento genéricamente
            // o solo si el apartmentId es de uno de sus apartamentos.
            // Esta lógica de permisos para "por apartamento" necesita definición clara.
            // Para simplificar, por ahora un no-admin no puede usar este filtro genérico.
            throw new AccessDeniedException("Only admins can filter payments by apartment ID directly. Use /me/payments for your payments.");
        }

        Page<Payment> payments = paymentRepository.findByApartmentId(apartmentId, pageable); // Ahora llamará al método correcto
        payments.forEach(this::initializeLazyFieldsForDto);
        return payments;
    }


    @Override
    @Transactional
    public Payment updatePaymentStatus(Long paymentId, PaymentStatus newStatus, String authenticatedUsername) {
        // El getPaymentById ahora necesitará UserDetails, pero aquí solo lo usa para la verificación de permisos
        // si el usuario que actualiza no es admin. Dado que este endpoint suele ser solo para admin,
        // podemos obtener el UserDetails del admin autenticado.
        // Si el servicio se llamara desde otro lugar sin UserDetails, habría que reconsiderar.
        // Por ahora, asumimos que el controlador pasa el UserDetails del admin.

        // Obtener el UserDetails del admin que está realizando la acción.
        // No podemos usar el 'authenticatedUsername' directamente para llamar a getPaymentById sin UserDetails.
        // Necesitamos que el controlador pase el UserDetails completo.
        // Solución temporal: obtener el UserDetails del admin aquí.
        // ¡OJO! Esto es una simplificación. Lo ideal es que el controlador pase el UserDetails.
        UserDetails adminUserDetails = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() != null ?
                (UserDetails) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;
        if (adminUserDetails == null || !adminUserDetails.getUsername().equals(authenticatedUsername)) {
            // Esto es una salvaguarda, pero el controlador debería manejar la autenticación.
            throw new AccessDeniedException("Authentication mismatch or missing for status update.");
        }

        Payment payment = getPaymentById(paymentId, adminUserDetails); // Usar el método con UserDetails

        User adminUser = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found: " + authenticatedUsername));
        initializeLazyFieldsForUser(adminUser); // Para el createdBy de la transacción


        PaymentStatus oldStatus = payment.getStatus();
        payment.setStatus(newStatus);
        Payment updatedPayment = paymentRepository.save(payment);

        if (newStatus == PaymentStatus.PAGADO && oldStatus != PaymentStatus.PAGADO) {
            Transaction paymentTransaction = Transaction.builder()
                    .type(TransactionType.PAGO_RECIBIDO)
                    .amount(updatedPayment.getAmount())
                    .description("Pago recibido: " + updatedPayment.getConcept())
                    .relatedPayment(updatedPayment)
                    .user(updatedPayment.getUser())
                    .apartment(updatedPayment.getApartment())
                    .createdBy(adminUser)
                    .build();
            transactionRepository.save(paymentTransaction);
        }
        initializeLazyFieldsForDto(updatedPayment); // Asegurar que el objeto devuelto esté listo para el mapper
        return updatedPayment;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Payment> getPaymentsByUserIdPaginated(Long userId, Pageable pageable, UserDetails authenticatedUserDetails) {
        boolean isAdmin = authenticatedUserDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        User userToFetchFor = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (!isAdmin) {
            User currentUser = userRepository.findByUsername(authenticatedUserDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
            if (!currentUser.getId().equals(userId)) {
                throw new AccessDeniedException("You can only view your own payments.");
            }
        }
        Page<Payment> payments = paymentRepository.findByUserId(userId, pageable);
        payments.forEach(this::initializeLazyFieldsForDto);
        return payments;
    }

    @Override
    @Transactional
    public void deletePayment(Long id) {
        if (!paymentRepository.findById(id).isPresent()){
            throw new ResourceNotFoundException("Payment not found with id: " + id);
        }
        paymentRepository.deleteById(id);
    }

    // Implementación de los métodos de la interfaz ManagePaymentUseCase que no estaban completos
    // Estos son los que devuelven List<Payment> y ahora deberían usar Page<Payment> o ser específicos.
    // Por consistencia, si el controlador espera Page, estos deberían devolver Page.
    // O, si son para uso interno y realmente necesitas List, está bien, pero considera la paginación.

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByUserId(Long userId, UserDetails authenticatedUserDetails) {
        // Aplicar la misma lógica de permisos que en getPaymentsByUserIdPaginated
        boolean isAdmin = authenticatedUserDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        User userToFetchFor = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (!isAdmin) {
            User currentUser = userRepository.findByUsername(authenticatedUserDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
            if (!currentUser.getId().equals(userId)) {
                throw new AccessDeniedException("You can only view your own payments.");
            }
        }
        List<Payment> payments = paymentRepository.findByUserId(userId); // Asume que este método existe y devuelve List
        payments.forEach(this::initializeLazyFieldsForDto);
        return payments;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByApartmentId(Long apartmentId, UserDetails authenticatedUserDetails) {
        // Aplicar lógica de permisos similar a getPaymentsByApartmentIdPaginated
        boolean isAdmin = authenticatedUserDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            // Lógica para verificar si el usuario tiene acceso a este apartamento
            // ... (requiere ResidencyRepositoryPort) ...
            throw new AccessDeniedException("Only admins can list payments by apartment ID directly.");
        }
        List<Payment> payments = paymentRepository.findByApartmentId(apartmentId); // Asume que este método existe y devuelve List
        payments.forEach(this::initializeLazyFieldsForDto);
        return payments;
    }
}