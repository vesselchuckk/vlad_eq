package ru.vlad.equipment_maintenance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vlad.equipment_maintenance.entity.SparePart;
import ru.vlad.equipment_maintenance.repository.SparePartRepository;

import java.util.List;

@RestController
@RequestMapping("/api/spare-parts")
@CrossOrigin(origins = "*")
public class SparePartController {

    private final SparePartRepository repository;

    public SparePartController(SparePartRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<SparePart> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public SparePart create(@RequestBody SparePart sparePart) {
        return repository.save(sparePart);
    }

    @PutMapping("/{id}")
    public SparePart update(@PathVariable Long id, @RequestBody SparePart sparePart) {
        sparePart.setId(id);
        return repository.save(sparePart);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}