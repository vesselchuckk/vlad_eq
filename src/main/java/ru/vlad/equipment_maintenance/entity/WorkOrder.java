package ru.vlad.equipment_maintenance.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "work_order")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
public class WorkOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    private String serviceType;     // "профилактика", "ремонт"
    private LocalDate scheduledDate;
    private String status;          // "planned", "in_progress", "done", "delayed_no_parts", "delayed_no_teams"

    @ManyToOne
    @JoinColumn(name = "service_team_id")
    private ServiceTeam serviceTeam; // Назначенная бригада
}