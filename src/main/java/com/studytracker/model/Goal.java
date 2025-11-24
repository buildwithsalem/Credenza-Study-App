package com.studytracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Goal name is required")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Target minutes is required")
    @Positive(message = "Target minutes must be positive")
    @Column(nullable = false)
    private Integer targetMinutes;

    @NotNull(message = "Goal type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalType type;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean active;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (active == null) {
            active = true;
        }
    }

    public enum GoalType {
        DAILY,
        WEEKLY,
        MONTHLY
    }
}
