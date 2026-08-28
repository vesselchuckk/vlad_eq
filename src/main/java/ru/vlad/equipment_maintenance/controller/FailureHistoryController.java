package ru.vlad.equipment_maintenance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vlad.equipment_maintenance.entity.FailureHistory;
import ru.vlad.equipment_maintenance.repository.FailureHistoryRepository;

import java.util.List;

@RestController
@RequestMapping("/api/failure-history")
@CrossOrigin(origins = "*")
public class FailureHistoryController {

    private final FailureHistoryRepository repository;

    public FailureHistoryController(FailureHistoryRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<FailureHistory> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public FailureHistory create(@RequestBody FailureHistory failureHistory) {
        return repository.save(failureHistory);
    }

    @PutMapping("/{id}")
    public FailureHistory update(@PathVariable Long id, @RequestBody FailureHistory failureHistory) {
        failureHistory.setId(id);
        return repository.save(failureHistory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}