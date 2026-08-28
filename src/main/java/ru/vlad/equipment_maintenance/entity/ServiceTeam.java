package ru.vlad.equipment_maintenance.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service_team")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
public class ServiceTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;          // Название бригады (например, "Бригада №1")
    private int maxActiveOrders;  // Максимальное кол-во одновременно выполняемых задач (например, 2)
    private int currentLoad;      // Текущее количество назначенных задач в статусе "planned" или "in_progress"

    @OneToMany(mappedBy = "serviceTeam")
    private List<WorkOrder> workOrders = new ArrayList<>();
}