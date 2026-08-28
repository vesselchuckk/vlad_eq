package ru.vlad.equipment_maintenance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vlad.equipment_maintenance.entity.WorkOrder;
import ru.vlad.equipment_maintenance.repository.WorkOrderRepository;

import java.util.List;

@RestController
@RequestMapping("/api/work-orders")
@CrossOrigin(origins = "*")
public class WorkOrderController {

    private final WorkOrderRepository repository;

    public WorkOrderController(WorkOrderRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<WorkOrder> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public WorkOrder create(@RequestBody WorkOrder workOrder) {
        return repository.save(workOrder);
    }

    @PutMapping("/{id}")
    public WorkOrder update(@PathVariable Long id, @RequestBody WorkOrder workOrder) {
        workOrder.setId(id);
        return repository.save(workOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}