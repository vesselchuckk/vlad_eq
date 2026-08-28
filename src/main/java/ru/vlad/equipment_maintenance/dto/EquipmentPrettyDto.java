package ru.vlad.equipment_maintenance.dto;

public class EquipmentPrettyDto {
    private Long id;
    private String name;
    private String status;
    private double failureRisk;

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getFailureRisk() { return failureRisk; }
    public void setFailureRisk(double failureRisk) { this.failureRisk = failureRisk; }
}