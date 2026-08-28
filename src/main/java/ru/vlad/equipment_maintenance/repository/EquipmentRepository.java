package ru.vlad.equipment_maintenance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vlad.equipment_maintenance.entity.Equipment;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
}