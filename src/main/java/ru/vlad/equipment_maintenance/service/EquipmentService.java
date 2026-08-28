package ru.vlad.equipment_maintenance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vlad.equipment_maintenance.dto.EquipmentPrettyDto;
import ru.vlad.equipment_maintenance.dto.ScheduleDto;
import ru.vlad.equipment_maintenance.entity.Equipment;
import ru.vlad.equipment_maintenance.entity.ServiceTeam;
import ru.vlad.equipment_maintenance.entity.SparePart;
import ru.vlad.equipment_maintenance.repository.EquipmentRepository;
import ru.vlad.equipment_maintenance.repository.ServiceTeamRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final ServiceTeamRepository serviceTeamRepository;

    @Autowired
    public EquipmentService(EquipmentRepository equipmentRepository, ServiceTeamRepository serviceTeamRepository) {
        this.equipmentRepository = equipmentRepository;
        this.serviceTeamRepository = serviceTeamRepository;
    }

    // Вспомогательный метод расчета риска отказа оборудования
    private double calculateFailureRisk(Equipment eq) {
        if (eq.getFailureHistory() == null || eq.getFailureHistory().isEmpty()) {
            return 0.1;
        }
        return Math.min(0.95, eq.getFailureHistory().size() * 0.2);
    }

    // 1. Метод построения оптимального графика ТО с учетом сервисных бригад
    @Transactional
    public List<ScheduleDto> buildOptimalSchedule() {
        List<Equipment> allEquipment = equipmentRepository.findAll();
        List<ServiceTeam> availableTeams = serviceTeamRepository.findAvailableTeams();
        List<ScheduleDto> scheduleList = new ArrayList<>();

        // Сортировка оборудования по убыванию риска отказа
        allEquipment.sort((e1, e2) -> Double.compare(calculateFailureRisk(e2), calculateFailureRisk(e1)));

        for (Equipment eq : allEquipment) {
            double risk = calculateFailureRisk(eq);
            String status = "Рекомендуется обслуживание";

            boolean hasParts = true;
            // Исправлено: если запчасти не требуются по умолчанию, то считаем, что они "есть"
            if (eq.getSpareParts() != null && !eq.getSpareParts().isEmpty()) {
                for (SparePart part : eq.getSpareParts()) {
                    if (part.getStockQuantity() <= 0) {
                        hasParts = false;
                        break;
                    }
                }
            }

            if (!hasParts) {
                status = "Отложено: нет нужных запчастей на складе";
            } else {
                // Поиск свободной бригады
                ServiceTeam freeTeam = availableTeams.stream()
                        .filter(team -> team.getCurrentLoad() < team.getMaxActiveOrders())
                        .findFirst()
                        .orElse(null);

                if (freeTeam != null) {
                    freeTeam.setCurrentLoad(freeTeam.getCurrentLoad() + 1);
                    status = "Запланировано (" + freeTeam.getName() + ")";
                } else {
                    status = "Отложено: все сервисные бригады заняты";
                }
            }

            ScheduleDto dto = new ScheduleDto();
            dto.setEquipmentId(eq.getId());
            dto.setEquipmentName(eq.getName());
            dto.setFailureRisk(risk);
            dto.setNextMaintenanceDate(LocalDate.now().plusDays(3));
            dto.setStatus(status);
            
            scheduleList.add(dto);
        }

        serviceTeamRepository.saveAll(availableTeams);
        return scheduleList;
    }

    // 2. Маппинг в EquipmentPrettyDto
    public List<EquipmentPrettyDto> getAllPretty() {
        return equipmentRepository.findAll().stream()
                .map(this::convertToPrettyDto)
                .collect(Collectors.toList());
    }

    // 3. Получение оборудования с высоким риском
    public List<EquipmentPrettyDto> getHighRiskEquipment() {
        return equipmentRepository.findAll().stream()
                .filter(eq -> calculateFailureRisk(eq) > 0.5)
                .map(this::convertToPrettyDto)
                .collect(Collectors.toList());
    }

    // 4. Общая статистика по системе (Добавлен расчет OEE для фронтенда)
    public Map<String, Object> getStats() {
        List<Equipment> allEquipment = equipmentRepository.findAll();
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalEquipment", allEquipment.size());
        stats.put("highRiskCount", allEquipment.stream().filter(eq -> calculateFailureRisk(eq) > 0.5).count());
        stats.put("averageRisk", allEquipment.stream().mapToDouble(this::calculateFailureRisk).average().orElse(0.0));
        
        // Симуляция и расчет базового OEE на основе простоев (базовый фонд 720 часов в месяц)
        double totalDowntime = allEquipment.stream()
                .filter(eq -> eq.getFailureHistory() != null)
                .flatMap(eq -> eq.getFailureHistory().stream())
                .mapToDouble(fh -> fh.getDowntimeHours())
                .sum();
        
        double nominalHours = Math.max(720.0, allEquipment.size() * 720.0);
        double oee = Math.max(0.4, (nominalHours - totalDowntime) / nominalHours);
        
        stats.put("oee", oee); // Ключ, который ожидает фронтенд в app.js
        
        return stats;
    }

    private EquipmentPrettyDto convertToPrettyDto(Equipment eq) {
        EquipmentPrettyDto dto = new EquipmentPrettyDto();
        dto.setId(eq.getId());
        dto.setName(eq.getName());
        dto.setStatus(eq.getStatus());
        dto.setFailureRisk(calculateFailureRisk(eq));
        return dto;
    }
}