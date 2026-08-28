package ru.vlad.equipment_maintenance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vlad.equipment_maintenance.entity.WorkOrder;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
}