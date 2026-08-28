package ru.vlad.equipment_maintenance.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "failure_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FailureHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    private LocalDate failureDate;
    private String description;
    private int downtimeHours;
}