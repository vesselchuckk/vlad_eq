package ru.vlad.equipment_maintenance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.vlad.equipment_maintenance.entity.FailureHistory;

public interface FailureHistoryRepository extends JpaRepository<FailureHistory, Long> {
}