package ru.vlad.equipment_maintenance.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "equipment")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String status;

    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FailureHistory> failureHistory;

    @ManyToMany
    @JoinTable(
        name = "equipment_spare_part",
        joinColumns = @JoinColumn(name = "equipment_id"),
        inverseJoinColumns = @JoinColumn(name = "spare_part_id")
    )
    private List<SparePart> spareParts;

    // СТАНДАРТНЫЙ КОНСТРУКТОР БЕЗ ПАРАМЕТРОВ
    public Equipment() {
    }

    // РУЧНЫЕ ГЕТТЕРЫ И СЕТТЕРЫ (VS Code их точно увидит)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<FailureHistory> getFailureHistory() {
        return this.failureHistory;
    }

    public void setFailureHistory(List<FailureHistory> failureHistory) {
        this.failureHistory = failureHistory;
    }

    public List<SparePart> getSpareParts() {
        return this.spareParts;
    }

    public void setSpareParts(List<SparePart> spareParts) {
        this.spareParts = spareParts;
    }
}