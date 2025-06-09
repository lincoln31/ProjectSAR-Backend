package com.conjuntoresidencial.api.domain.property.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "towers")
@ToString(exclude = {"apartments"}) // EXCLUIR LA COLECCIÓN
@EqualsAndHashCode(exclude = {"apartments"}) // EXCLUIR LA COLECCIÓN
public class Tower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name; // Ej: "Torre A", "Bloque Esmeralda"

    @Column(unique = true, length = 50, nullable = true) // Opcional y único si se usa
    private String identifier; // Ej: "T1", "A-01"

    @Column(length = 255, nullable = true) // La dirección puede ser general del conjunto o específica de la torre
    private String address;

    @Column(name = "number_of_floors", nullable = true)
    private Integer numberOfFloors;

    @Column(name = "apartments_per_floor", nullable = true) // Si la distribución es uniforme
    private Integer apartmentsPerFloor;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relación: Una Torre tiene muchos Apartamentos
    // mappedBy: "tower" es el nombre del campo en la entidad Apartment que mapea esta relación.
    // cascade: Si se elimina una torre, ¿qué pasa con sus apartamentos?
    //          CascadeType.ALL es fuerte (borra apartamentos). Podrías querer un manejo más específico
    //          o CascadeType.PERSIST, CascadeType.MERGE si los apartamentos se gestionan por separado
    //          y no quieres que se borren automáticamente. Por ahora, si una torre se borra,
    //          probablemente sus apartamentos también deberían (o marcarse como inválidos).
    // orphanRemoval = true: Si un apartamento se desvincula de la lista de esta torre (ej. apartments.remove(apto)), se eliminará de la BD.
    @OneToMany(mappedBy = "tower", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference("tower-apartments") // Para Jackson
    private List<Apartment> apartments = new ArrayList<>();


    // Métodos ayudantes para la relación bidireccional (opcional pero útil)
    public void addApartment(Apartment apartment) {
        apartments.add(apartment);
        apartment.setTower(this);
    }

    public void removeApartment(Apartment apartment) {
        apartments.remove(apartment);
        apartment.setTower(null);
    }
}