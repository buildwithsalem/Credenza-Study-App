package com.studytracker.repository;

import com.studytracker.model.StudySession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    List<StudySession> findAllByOrderByStartTimeDesc();

    List<StudySession> findByStartTimeBetweenOrderByStartTimeDesc(
            LocalDateTime start, LocalDateTime end);

    @Query("SELECT s.subject, SUM(s.durationMinutes) FROM StudySession s " +
           "WHERE s.startTime >= :startDate " +
           "GROUP BY s.subject " +
           "ORDER BY SUM(s.durationMinutes) DESC")
    List<Object[]> getStudyTimeBySubject(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT SUM(s.durationMinutes) FROM StudySession s " +
           "WHERE s.startTime >= :startDate")
    Integer getTotalStudyTime(@Param("startDate") LocalDateTime startDate);
}
