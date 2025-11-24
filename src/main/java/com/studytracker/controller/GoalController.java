package com.studytracker.controller;

import com.studytracker.model.Goal;
import com.studytracker.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
@CrossOrigin(origins = "*")
public class GoalController {

    @Autowired
    private GoalService service;

    @PostMapping
    public ResponseEntity<Goal> createGoal(@Valid @RequestBody Goal goal) {
        Goal created = service.createGoal(goal);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Goal>> getAllGoals() {
        return ResponseEntity.ok(service.getAllGoals());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Goal>> getActiveGoals() {
        return ResponseEntity.ok(service.getActiveGoals());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Goal> getGoalById(@PathVariable Long id) {
        Goal goal = service.getGoalById(id);
        if (goal == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(goal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Goal> updateGoal(@PathVariable Long id, @Valid @RequestBody Goal goal) {
        Goal updated = service.updateGoal(id, goal);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        service.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<Map<String, Object>> getGoalProgress(@PathVariable Long id) {
        Map<String, Object> progress = service.getGoalProgress(id);
        if (progress == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(progress);
    }
}
