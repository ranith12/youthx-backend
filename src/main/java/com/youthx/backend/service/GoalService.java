package com.youthx.backend.service;


import com.youthx.backend.entity.Goal;
import com.youthx.backend.exception.ResourceOwnershipException;
import com.youthx.backend.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;

    public Goal create(UUID currentUserId, Goal goal) {
        goal.setUserId(currentUserId);
        goal.setCreatedAt(OffsetDateTime.now());
        if (goal.getProgressPercent() == null) {
            goal.setProgressPercent(0);
        }
        return goalRepository.save(goal);
    }

    public List<Goal> listByUser(UUID currentUserId) {
        return goalRepository.findByUserId(currentUserId);
    }

    public Goal getById(UUID currentUserId, Long goalId) {
        Goal existing = goalRepository.findById(goalId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Goal not found with id: " + goalId
                ));

        if (!existing.getUserId().equals(currentUserId)) {
            throw new ResourceOwnershipException("User does not own this goal");
        }

        return existing;
    }

    public Goal update(UUID currentUserId, Long goalId, Goal goal) {
        Goal existing = goalRepository.findById(goalId)
                .orElseThrow(() -> new NoSuchElementException("Goal not found with id: " + goalId));

        if (!existing.getUserId().equals(currentUserId)) {
            throw new ResourceOwnershipException("User does not own this goal");
        }

        existing.setName(goal.getName());
        existing.setCategory(goal.getCategory());
        existing.setTargetDate(goal.getTargetDate());
        existing.setProgressPercent(goal.getProgressPercent());
        existing.setDailyReminderTime(goal.getDailyReminderTime());
        existing.setUpdatedAt(OffsetDateTime.now());

        return goalRepository.save(existing);
    }

    public void delete(UUID currentUserId, Long goalId) {
        Goal existing = goalRepository.findById(goalId)
                .orElseThrow(() -> new NoSuchElementException("Goal not found with id: " + goalId));

        if (!existing.getUserId().equals(currentUserId)) {
            throw new ResourceOwnershipException("User does not own this goal");
        }

        goalRepository.delete(existing);
    }
}
