package com.studytracker.service;

import com.studytracker.model.Goal;
import com.studytracker.repository.GoalRepository;
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
public class GoalService {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private StudySessionRepository sessionRepository;

    public Goal createGoal(Goal goal) {
        return goalRepository.save(goal);
    }

    public List<Goal> getAllGoals() {
        return goalRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Goal> getActiveGoals() {
        return goalRepository.findByActiveOrderByCreatedAtDesc(true);
    }

    public Goal getGoalById(Long id) {
        return goalRepository.findById(id).orElse(null);
    }

    public void deleteGoal(Long id) {
        goalRepository.deleteById(id);
    }

    public Goal updateGoal(Long id, Goal goalDetails) {
        Goal goal = goalRepository.findById(id).orElse(null);
        if (goal != null) {
            goal.setName(goalDetails.getName());
            goal.setTargetMinutes(goalDetails.getTargetMinutes());
            goal.setType(goalDetails.getType());
            goal.setActive(goalDetails.getActive());
            return goalRepository.save(goal);
        }
        return null;
    }

    public Map<String, Object> getGoalProgress(Long goalId) {
        Goal goal = goalRepository.findById(goalId).orElse(null);
        if (goal == null) {
            return null;
        }

        LocalDateTime startDate = calculateGoalStartDate(goal.getType());
        Integer studyMinutes = sessionRepository.getTotalStudyTime(startDate);
        
        Map<String, Object> progress = new HashMap<>();
        progress.put("goal", goal);
        progress.put("currentMinutes", studyMinutes != null ? studyMinutes : 0);
        progress.put("targetMinutes", goal.getTargetMinutes());
        progress.put("percentage", calculatePercentage(studyMinutes, goal.getTargetMinutes()));
        
        return progress;
    }

    private LocalDateTime calculateGoalStartDate(Goal.GoalType type) {
        LocalDateTime now = LocalDateTime.now();
        switch (type) {
            case DAILY:
                return now.withHour(0).withMinute(0).withSecond(0);
            case WEEKLY:
                return now.minusWeeks(1);
            case MONTHLY:
                return now.minusMonths(1);
            default:
                return now.minusWeeks(1);
        }
    }

    private double calculatePercentage(Integer current, Integer target) {
        if (target == null || target == 0) {
            return 0.0;
        }
        int currentValue = current != null ? current : 0;
        return Math.min(100.0, (currentValue * 100.0) / target);
    }
}
