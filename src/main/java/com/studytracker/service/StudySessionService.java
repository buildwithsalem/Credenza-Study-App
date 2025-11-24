package com.studytracker.service;

import com.studytracker.model.StudySession;
import com.studytracker.repository.StudySessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class StudySessionService {

    @Autowired
    private StudySessionRepository repository;

    public StudySession createSession(StudySession session) {
        return repository.save(session);
    }

    public List<StudySession> getAllSessions() {
        return repository.findAllByOrderByStartTimeDesc();
    }

    public StudySession getSessionById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void deleteSession(Long id) {
        repository.deleteById(id);
    }

    public List<StudySession> getSessionsInRange(LocalDateTime start, LocalDateTime end) {
        return repository.findByStartTimeBetweenOrderByStartTimeDesc(start, end);
    }

    public Map<String, Object> getAnalytics(String period) {
        LocalDateTime startDate = calculateStartDate(period);
        
        List<Object[]> subjectData = repository.getStudyTimeBySubject(startDate);
        Integer totalMinutes = repository.getTotalStudyTime(startDate);
        
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalMinutes", totalMinutes != null ? totalMinutes : 0);
        analytics.put("subjectBreakdown", subjectData);
        analytics.put("period", period);
        
        return analytics;
    }

    private LocalDateTime calculateStartDate(String period) {
        LocalDateTime now = LocalDateTime.now();
        switch (period.toLowerCase()) {
            case "daily":
                return now.minusDays(1);
            case "weekly":
                return now.minusWeeks(1);
            case "monthly":
                return now.minusMonths(1);
            default:
                return now.minusWeeks(1);
        }
    }
}
