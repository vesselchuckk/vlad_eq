package ru.vlad.equipment_maintenance.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ScheduleDto {
    private Long equipmentId;
    private String equipmentName;
    private LocalDate nextMaintenanceDate;
    private double failureRisk;
    private String status; // Отобразит: "Запланировано (Бригада №1)", "Отложено: нет нужных запчастей на складе" и т.д.
}