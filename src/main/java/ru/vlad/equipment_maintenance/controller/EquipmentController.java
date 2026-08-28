package ru.vlad.equipment_maintenance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.vlad.equipment_maintenance.dto.EquipmentPrettyDto;
import ru.vlad.equipment_maintenance.dto.ScheduleDto;
import ru.vlad.equipment_maintenance.service.EquipmentService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping("/pretty")
    public List<EquipmentPrettyDto> getAllPretty() {
        return equipmentService.getAllPretty();
    }

    @GetMapping("/high-risk")
    public List<EquipmentPrettyDto> getHighRiskEquipment() {
        return equipmentService.getHighRiskEquipment();
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return equipmentService.getStats();
    }

    @GetMapping("/schedule")
    public List<ScheduleDto> getSchedule() {
        return equipmentService.buildOptimalSchedule();
    }
}