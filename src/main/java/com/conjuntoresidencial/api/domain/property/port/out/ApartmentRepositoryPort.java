package com.conjuntoresidencial.api.domain.property.port.out;
import com.conjuntoresidencial.api.domain.property.model.Apartment;
import com.conjuntoresidencial.api.domain.property.model.Tower;
import java.util.List;
import java.util.Optional;

public interface ApartmentRepositoryPort {
    Apartment save(Apartment apartment);
    Optional<Apartment> findById(Long id);
    List<Apartment> findAll();
    List<Apartment> findByTower(Tower tower); // Para listar apartamentos de una torre específica
    Optional<Apartment> findByNumberAndTower(String number, Tower tower); // Para unicidad
    void deleteById(Long id);
}