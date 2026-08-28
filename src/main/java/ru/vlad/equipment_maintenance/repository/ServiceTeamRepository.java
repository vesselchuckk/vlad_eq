package ru.vlad.equipment_maintenance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.vlad.equipment_maintenance.entity.ServiceTeam;
import java.util.List;

@Repository
public interface ServiceTeamRepository extends JpaRepository<ServiceTeam, Long> {
    
    // Оставляем ТОЛЬКО этот метод. Старый findByCurrentLoad... удаляем полностью!
    @Query("SELECT t FROM ServiceTeam t WHERE t.currentLoad < t.maxActiveOrders")
    List<ServiceTeam> findAvailableTeams();
}