package com.conjuntoresidencial.api.infrastructure.persistence.specification;


import com.conjuntoresidencial.api.domain.property.model.Apartment;
import com.conjuntoresidencial.api.domain.property.model.Tower;
import com.conjuntoresidencial.api.domain.residency.model.Residency;
import com.conjuntoresidencial.api.domain.user.model.User;
import com.conjuntoresidencial.api.domain.user.model.UserProfile;
import com.conjuntoresidencial.api.domain.vehicle.model.Vehicle;
import com.conjuntoresidencial.api.domain.vehicle.model.VehicleStatus;
import com.conjuntoresidencial.api.domain.vehicle.model.VehicleType;
import jakarta.persistence.criteria.*;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class VehicleSpecification {

    public static Specification<Vehicle> byCriteria(
            String plate, Long towerId, Long apartmentId, VehicleType type,
            VehicleStatus status, String residentNameOrDocument) {

        return (Root<Vehicle> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(plate)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("plate")), "%" + plate.toLowerCase() + "%"));
            }
            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            // Para filtros por residente, torre o apartamento, necesitamos joins
            if (StringUtils.hasText(residentNameOrDocument) || towerId != null || apartmentId != null) {
                Join<Vehicle, User> ownerJoin = root.join("owner", JoinType.INNER); // Vehicle -> User (owner)
                Join<User, UserProfile> userProfileJoin = ownerJoin.join("userProfile", JoinType.LEFT); // User -> UserProfile
                // Para vincular a torre/apartamento, necesitamos la entidad Residency
                // Esto asume que un usuario puede tener varias residencias, pero para el vehículo
                // probablemente nos interese su residencia "principal" o cualquiera activa.
                // Simplificación: asumimos que podemos encontrar una Residency activa.
                // Una forma más precisa podría requerir un subquery o una lógica de negocio más compleja
                // para determinar el apartamento "actual" del dueño del vehículo.
                // Por ahora, unamos a través de Residency si se filtra por torre/apto.

                if (StringUtils.hasText(residentNameOrDocument)) {
                    Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(userProfileJoin.get("firstName")), "%" + residentNameOrDocument.toLowerCase() + "%");
                    Predicate lastNamePredicate = criteriaBuilder.like(criteriaBuilder.lower(userProfileJoin.get("lastName")), "%" + residentNameOrDocument.toLowerCase() + "%");
                    Predicate documentPredicate = criteriaBuilder.like(criteriaBuilder.lower(userProfileJoin.get("documentId")), "%" + residentNameOrDocument.toLowerCase() + "%");
                    Predicate usernamePredicate = criteriaBuilder.like(criteriaBuilder.lower(ownerJoin.get("username")), "%" + residentNameOrDocument.toLowerCase() + "%");
                    predicates.add(criteriaBuilder.or(namePredicate, lastNamePredicate, documentPredicate, usernamePredicate));
                }

                // Filtro por Torre y/o Apartamento (requiere un join a través de Residency)
                // Esto es más complejo porque un User puede tener varias residencias.
                // Para una implementación simple y directa, si necesitas estos filtros,
                // podrías considerar añadir una referencia directa de Vehicle a Apartment si un vehículo
                // está estrictamente ligado a un solo apartamento (además de su dueño User).
                // O, si un vehículo puede ser usado por un residente en cualquiera de sus apartamentos,
                // la consulta se vuelve más compleja y podría requerir subconsultas o una
                // desnormalización o un campo "primaryApartmentId" en User.

                // Por ahora, si towerId o apartmentId vienen, vamos a necesitar un Join a Residency
                // y luego a Apartment y Tower. Esta parte es la más compleja de la especificación.
                // Una simplificación sería buscar usuarios que VIVEN en esa torre/apartamento
                // y luego los vehículos de esos usuarios.

                if (towerId != null || apartmentId != null) {
                    // Este join asume que queremos vehículos de usuarios que tienen una residencia
                    // en el apartamento/torre especificados.
                    Join<User, Residency> residencyJoin = ownerJoin.join("residencies", JoinType.INNER); // User -> Residency
                    Join<Residency, Apartment> apartmentJoin = residencyJoin.join("apartment", JoinType.INNER); // Residency -> Apartment

                    if (apartmentId != null) {
                        predicates.add(criteriaBuilder.equal(apartmentJoin.get("id"), apartmentId));
                    } else if (towerId != null) { // Solo si apartmentId no está presente
                        Join<Apartment, Tower> towerJoin = apartmentJoin.join("tower", JoinType.INNER); // Apartment -> Tower
                        predicates.add(criteriaBuilder.equal(towerJoin.get("id"), towerId));
                    }
                }
            }
            // Evitar duplicados si hay múltiples joins que podrían causar filas repetidas
            query.distinct(true);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}