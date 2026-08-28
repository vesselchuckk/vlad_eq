package ru.vlad.equipment_maintenance.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "spare_part")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SparePart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    private String name;
    private int stockQuantity;      // Остаток на складе
}