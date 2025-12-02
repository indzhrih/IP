package org.example.sortapp.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SortStatistics {

    private final LocalDateTime startedAt;
    private final LocalDateTime finishedAt;

    public SortStatistics(LocalDateTime startedAt, LocalDateTime finishedAt) {
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public Duration getDuration() {
        if (startedAt == null || finishedAt == null) {
            return Duration.ZERO;
        }
        return Duration.between(startedAt, finishedAt);
    }
}
