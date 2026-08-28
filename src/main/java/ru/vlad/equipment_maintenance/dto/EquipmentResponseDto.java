package ru.vlad.equipment_maintenance.dto;

import java.time.LocalDate;

public record EquipmentResponseDto(
        Long id,
        String name,
        String serialNumber,
        String manufacturer,
        int operatingHours,
        int priority,
        LocalDate lastMaintenanceDate,
        int failureCount,           // сколько было отказов
        double failureRisk,         // рассчитанный риск
        String riskLevel            // "Низкий", "Средний", "Высокий", "Критический"
) {}