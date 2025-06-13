package com.conjuntoresidencial.api.application.vehicle.service;

import com.conjuntoresidencial.api.domain.residency.model.Residency; // Necesario para obtener torre/apto del owner
import com.conjuntoresidencial.api.domain.residency.port.out.ResidencyRepositoryPort; // Necesario
import com.conjuntoresidencial.api.domain.user.model.User;
import com.conjuntoresidencial.api.domain.user.model.UserProfile;
import com.conjuntoresidencial.api.domain.user.port.out.UserProfileRepositoryPort;
import com.conjuntoresidencial.api.domain.user.port.out.UserRepositoryPort;
import com.conjuntoresidencial.api.domain.vehicle.model.Vehicle;
import com.conjuntoresidencial.api.domain.vehicle.model.VehicleStatus;
import com.conjuntoresidencial.api.domain.vehicle.model.VehicleType;
import com.conjuntoresidencial.api.domain.vehicle.port.in.ManageVehicleUseCase;
import com.conjuntoresidencial.api.domain.vehicle.port.out.VehicleRepositoryPort;
import com.conjuntoresidencial.api.domain.shared.exception.ResourceNotFoundException;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.VehicleRequestDto;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.VehicleResponseDto;
import com.conjuntoresidencial.api.infrastructure.web.mapper.VehicleMapper;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleManagementService implements ManageVehicleUseCase {
    private final VehicleRepositoryPort vehicleRepository;
    private final UserRepositoryPort userRepository;
    private final UserProfileRepositoryPort userProfileRepository; // Inyectar este
    private final VehicleMapper vehicleMapper; // Asumiendo
    private final ResidencyRepositoryPort residencyRepository; // Para obtener datos de torre/apto


    @Override
    @Transactional
    public Vehicle createVehicle(VehicleRequestDto vehicleDto) {
      //  logger.debug("Attempting to create vehicle with DTO: {}", vehicleDto);

        // 1. Buscar el UserProfile por el documentId proporcionado
        UserProfile residentProfile = userProfileRepository.findByDocumentId(vehicleDto.getResidentDocumentId())
                .orElseThrow(() -> {
                    //logger.warn("Resident (UserProfile) not found with document ID: {}", vehicleDto.getResidentDocumentId());
                    return new ResourceNotFoundException(
                            "Resident not found with document ID: " + vehicleDto.getResidentDocumentId());
                });

        // 2. Obtener el User (residente) desde el UserProfile
        User resident = residentProfile.getUser();
        if (resident == null) {
            // Este caso es muy improbable si la relación UserProfile -> User está bien definida (nullable=false en UserProfile.user)
            // y UserProfile siempre se crea asociado a un User.
            //logger.error("UserProfile with document ID {} found but is not associated with a User.", vehicleDto.getResidentDocumentId());
            throw new IllegalStateException("UserProfile found (document ID: " + vehicleDto.getResidentDocumentId() + ") but is not associated with a User.");
        }

        // 3. Forzar la inicialización del UserProfile del residente ANTES de cualquier uso posterior
        // Esto es importante si el VehicleMapper o código subsiguiente necesita acceder a los detalles del perfil del dueño.
        // Nota: residentProfile ya está cargado (no es LAZY desde User, sino que User es LAZY desde UserProfile).
        // Pero si el User fue cargado perezosamente en algún punto y luego se accede a su UserProfile,
        // debemos asegurar que el UserProfile asociado al 'resident' (que será el 'owner') esté cargado.
        // Si residentProfile.getUser() te da un proxy de User, necesitas inicializar el UserProfile de ese User.
        // La línea `Hibernate.initialize(resident.getUserProfile());` es la forma explícita.
        // Si residentProfile es la entidad completa y resident es la entidad completa, esto ya está bien.
        // El UserProfile que tenemos (residentProfile) es el que se asociará al User (resident).
        // El objeto 'resident' (User) que obtenemos de 'residentProfile.getUser()' podría ser un proxy si la relación UserProfile->User es LAZY.
        // Y el 'userProfile' DENTRO de la entidad 'resident' (User) también es LAZY.

        // Si residentProfile.getUser() devuelve un User que es un proxy, y User.userProfile es LAZY,
        // necesitamos asegurarnos de que cuando el mapper acceda a vehicle.getOwner().getUserProfile(),
        // esa relación LAZY esté cargada.
        // El 'resident' ya tiene su 'residentProfile' cargado porque lo obtuvimos directamente.
        // La clave es lo que el mapper necesita del 'owner' (que es 'resident').

        // Vamos a asegurarnos de que el UserProfile *dentro* del objeto User 'resident' esté inicializado.
        if (resident.getUserProfile() == null) {
            // Si UserProfile es LAZY en User y no se ha seteado bidireccionalmente
            // o si el User fue cargado sin su UserProfile.
            // Sin embargo, dado que obtuvimos User DESDE UserProfile, UserProfile debería ser 'residentProfile'.
            // Y resident.getUserProfile() debería devolver este mismo 'residentProfile' si la relación bidireccional está bien
            // y si la carga de 'resident' no fue tan perezosa como para no incluirlo.
            // Vamos a asumir que resident.getUserProfile() devuelve el 'residentProfile' que ya tenemos.
            // Si no, necesitaríamos algo como:
            // Hibernate.initialize(resident.getUserProfile());
            // o simplemente:
            // resident.getUserProfile().getFirstName(); // Acceso para inicializar
            // Por ahora, confiamos en que el 'resident' ya tiene su UserProfile (el 'residentProfile') accesible.
        } else {
            // Si ya existe, solo nos aseguramos que no sea un proxy sin inicializar.
            Hibernate.initialize(resident.getUserProfile());
           // logger.debug("UserProfile for resident {} (ID: {}) initialized.", resident.getUsername(), resident.getId());
        }


        // 4. Verificar si el vehículo (por placa) ya existe
        // Asegúrate que el método en el repositorio sea `existsByLicensePlate`
        if (vehicleRepository.existsByLicensePlate(vehicleDto.getLicensePlate())) {
            //logger.warn("Vehicle with license plate {} already exists.", vehicleDto.getLicensePlate());
            throw new IllegalArgumentException(
                    "Vehicle with license plate " + vehicleDto.getLicensePlate() + " already exists.");
        }

        // 5. Mapear DTO a entidad de dominio Vehicle
        Vehicle vehicle = vehicleMapper.toDomain(vehicleDto);
        vehicle.setOwner(resident); // Establecer la relación con el User (residente)
        // vehicle.setType(vehicleDto.getType()); // Asegúrate que el mapper o la entidad lo manejen

        // 6. Guardar el vehículo
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        //logger.info("Vehicle created successfully with ID: {}", savedVehicle.getId());

        // 7. FORZAR LA CARGA DE RELACIONES LAZY DEL VEHÍCULO GUARDADO ANTES DE DEVOLVERLO
        // Esto es lo más importante para que el mapper en el controlador no falle.
        // El objeto 'savedVehicle' que devolvemos debe tener su 'owner' y el 'userProfile' del 'owner'
        // completamente inicializados si el mapper los va a usar.
        if (savedVehicle.getOwner() != null) {
            Hibernate.initialize(savedVehicle.getOwner()); // Inicializa el proxy de User si es LAZY desde Vehicle
            if (savedVehicle.getOwner().getUserProfile() != null) {
                Hibernate.initialize(savedVehicle.getOwner().getUserProfile()); // Inicializa el UserProfile del User
               // logger.debug("Owner's UserProfile (ID: {}) initialized for saved vehicle ID: {}",
                       // savedVehicle.getOwner().getUserProfile().getId(), savedVehicle.getId());
            } else {
                // Esto sería extraño si el registro siempre crea UserProfile,
                // pero podría pasar si un User se creó sin UserProfile por otro medio.
                //logger.warn("Saved vehicle's owner (ID: {}) does not have an associated UserProfile.", savedVehicle.getOwner().getId());
            }
        }

        return savedVehicle;
    }
    // Método para enriquecer VehicleResponseDto con info de torre/apartamento
    private VehicleResponseDto enrichVehicleResponseDto(Vehicle vehicle) {
        VehicleResponseDto dto = vehicleMapper.toDto(vehicle);
        if (vehicle.getOwner() != null) {
            // Buscar una residencia activa/principal del dueño para obtener torre/apto.
            // Esta lógica puede ser compleja y depende de cómo definas "residencia principal".
            // Simplificación: tomar la primera residencia encontrada (si hay).
            Optional<Residency> ownerResidency = residencyRepository.findByUser(vehicle.getOwner())
                    .stream().findFirst(); // O filtrar por activa, etc.
            if (ownerResidency.isPresent() && ownerResidency.get().getApartment() != null) {
                dto.setApartmentNumber(ownerResidency.get().getApartment().getNumber());
                if (ownerResidency.get().getApartment().getTower() != null) {
                    dto.setTowerName(ownerResidency.get().getApartment().getTower().getName());
                }
            }
        }
        return dto;
    }


    @Transactional(readOnly = true)
    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
    }


    @Override
    @Transactional(readOnly = true)
    public Vehicle getVehicleByPlate(String plate) {
        Vehicle vehicle = vehicleRepository.findByPlate(plate)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with plate: " + plate));
        if (vehicle.getOwner() != null && vehicle.getOwner().getUserProfile() != null) {
            vehicle.getOwner().getUserProfile().getFirstName();
        }
        return vehicle;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehicleResponseDto> getAllVehiclesFiltered(
            String plate, Long towerId, Long apartmentId, VehicleType type,
            VehicleStatus status, String residentNameOrDocument, Pageable pageable) {

        Page<Vehicle> vehiclesPage = vehicleRepository.findByCriteria(
                plate, towerId, apartmentId, type, status, residentNameOrDocument, pageable
        );
        // Mapear y enriquecer cada vehículo
        return vehiclesPage.map(this::enrichVehicleResponseDto);
    }

    @Override
    @Transactional
    public Vehicle updateVehicle(Long vehicleId, VehicleRequestDto vehicleDto) {
        // 1. Obtener el vehículo existente
        Vehicle existingVehicle = getVehicleById(vehicleId);

        // 2. Buscar el nuevo propietario (User) usando el residentDocumentId del DTO
        UserProfile newOwnerProfile = userProfileRepository.findByDocumentId(vehicleDto.getResidentDocumentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resident (new owner) not found with document ID: " + vehicleDto.getResidentDocumentId()));

        User newOwner = newOwnerProfile.getUser();
        if (newOwner == null) {
            throw new IllegalStateException("UserProfile found for new owner but is not associated with a User.");
        }

        // 3. Verificar si la placa cambia y si la nueva placa ya existe en OTRO vehículo
        if (!existingVehicle.getLicensePlate().equalsIgnoreCase(vehicleDto.getLicensePlate())) {
            // Si la placa cambió, verificar que la nueva no esté en uso por otro vehículo
            vehicleRepository.findByLicensePlate(vehicleDto.getLicensePlate()).ifPresent(vehicleWithNewPlate -> {
                if (!vehicleWithNewPlate.getId().equals(existingVehicle.getId())) {
                    throw new IllegalArgumentException(
                            "Another vehicle with license plate '" + vehicleDto.getLicensePlate() + "' already exists.");
                }
            });
        }

        // 4. Actualizar los campos del vehículo desde el DTO usando el mapper
        // El mapper está configurado para ignorar 'owner' (target = "owner", ignore = true)
        vehicleMapper.updateDomainFromDto(vehicleDto, existingVehicle);

        // 5. Asignar (o re-asignar) el propietario
        existingVehicle.setOwner(newOwner);

        // 6. Asignar el tipo de vehículo (si no lo hace el mapper directamente)
        // Si tu VehicleMapper.updateDomainFromDto no mapea 'type', hazlo aquí:
        // existingVehicle.setType(vehicleDto.getType());
        // Pero es mejor si el mapper lo maneja. Asegúrate de que VehicleRequestDto tenga el campo 'type'
        // y que VehicleMapper lo mapee (o que el campo se llame igual en DTO y entidad)

        return vehicleRepository.save(existingVehicle);
    }

    @Override
    @Transactional
    public void deleteVehicle(Long id) {
        if (!vehicleRepository.findById(id).isPresent()){
            throw new ResourceNotFoundException("Vehicle not found with id: " + id);
        }
        vehicleRepository.deleteById(id);
    }
}