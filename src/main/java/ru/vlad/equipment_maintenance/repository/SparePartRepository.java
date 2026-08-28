package ru.vlad.equipment_maintenance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vlad.equipment_maintenance.entity.SparePart;

public interface SparePartRepository extends JpaRepository<SparePart, Long> {
}