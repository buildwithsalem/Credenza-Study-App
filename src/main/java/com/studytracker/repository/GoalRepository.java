package com.studytracker.repository;

import com.studytracker.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByActiveOrderByCreatedAtDesc(Boolean active);

    List<Goal> findAllByOrderByCreatedAtDesc();
}
