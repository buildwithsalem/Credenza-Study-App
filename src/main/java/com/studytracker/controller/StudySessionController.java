package com.studytracker.controller;

import com.studytracker.model.StudySession;
import com.studytracker.service.StudySessionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
@CrossOrigin(origins = "*")
public class StudySessionController {

    @Autowired
    private StudySessionService service;

    @PostMapping
    public ResponseEntity<StudySession> createSession(@Valid @RequestBody StudySession session) {
        StudySession created = service.createSession(session);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<StudySession>> getAllSessions() {
        return ResponseEntity.ok(service.getAllSessions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudySession> getSessionById(@PathVariable Long id) {
        StudySession session = service.getSessionById(id);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(session);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        service.deleteSession(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/analytics/{period}")
    public ResponseEntity<Map<String, Object>> getAnalytics(@PathVariable String period) {
        return ResponseEntity.ok(service.getAnalytics(period));
    }
}
