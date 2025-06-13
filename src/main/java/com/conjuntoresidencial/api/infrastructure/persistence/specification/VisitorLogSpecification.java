package com.conjuntoresidencial.api.infrastructure.persistence.specification;


import com.conjuntoresidencial.api.domain.visitorlog.model.VisitorLog;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VisitorLogSpecification {

    public static Specification<VisitorLog> byCriteria(
            String visitorName, Long residentVisitedId, Long apartmentVisitedId,
            LocalDateTime entryDateFrom, LocalDateTime entryDateTo, Boolean currentlyInside) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(visitorName)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("visitorFullName")), "%" + visitorName.toLowerCase() + "%"));
            }
            if (residentVisitedId != null) {
                predicates.add(criteriaBuilder.equal(root.get("residentVisited").get("id"), residentVisitedId));
            }
            if (apartmentVisitedId != null) {
                predicates.add(criteriaBuilder.equal(root.get("apartmentVisited").get("id"), apartmentVisitedId));
            }
            if (entryDateFrom != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("entryTimestamp"), entryDateFrom));
            }
            if (entryDateTo != null) {
                // Para incluir todo el día 'entryDateTo', ajustamos a fin de día
                LocalDateTime endOfDay = entryDateTo.toLocalDate().atTime(23, 59, 59);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("entryTimestamp"), endOfDay));
            }
            if (currentlyInside != null) {
                if (currentlyInside) {
                    predicates.add(criteriaBuilder.isNull(root.get("exitTimestamp")));
                } else {
                    predicates.add(criteriaBuilder.isNotNull(root.get("exitTimestamp")));
                }
            }

            query.orderBy(criteriaBuilder.desc(root.get("entryTimestamp"))); // Ordenar por más reciente
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}