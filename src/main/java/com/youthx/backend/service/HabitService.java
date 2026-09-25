package com.youthx.backend.service;


import com.youthx.backend.entity.Habit;
import com.youthx.backend.exception.ResourceOwnershipException;
import com.youthx.backend.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepository;

    public Habit create(UUID currentUserId, Habit habit) {
        habit.setUserId(currentUserId);
        habit.setCreatedAt(OffsetDateTime.now());
        if (habit.getCurrentStreak() == null) {
            habit.setCurrentStreak(0);
        }
        return habitRepository.save(habit);
    }

    public List<Habit> listByUser(UUID currentUserId) {
        return habitRepository.findByUserId(currentUserId);
    }

    public Habit update(UUID currentUserId, Long habitId, Habit habit) {
        Habit existing = habitRepository.findById(habitId)
                .orElseThrow(() -> new NoSuchElementException("Habit not found with id: " + habitId));

        if (!existing.getUserId().equals(currentUserId)) {
            throw new ResourceOwnershipException("User does not own this habit");
        }

        existing.setName(habit.getName());
        existing.setEmoji(habit.getEmoji());
        existing.setColor(habit.getColor());
        existing.setFrequency(habit.getFrequency());
        existing.setTimeOfDay(habit.getTimeOfDay());

        return habitRepository.save(existing);
    }

    public void delete(UUID currentUserId, Long habitId) {
        Habit existing = habitRepository.findById(habitId)
                .orElseThrow(() -> new NoSuchElementException("Habit not found with id: " + habitId));

        if (!existing.getUserId().equals(currentUserId)) {
            throw new ResourceOwnershipException("User does not own this habit");
        }

        habitRepository.delete(existing);
    }

    /** Marks today as completed for this habit and grows the streak. */
    @Transactional
    public Habit complete(UUID currentUserId, Long habitId) {
        Habit existing = habitRepository.findById(habitId)
                .orElseThrow(() -> new NoSuchElementException("Habit not found with id: " + habitId));

        if (!existing.getUserId().equals(currentUserId)) {
            throw new ResourceOwnershipException("User does not own this habit");
        }

        LocalDate today = LocalDate.now();
        if (!today.equals(existing.getLastCompletedAt())) {
            int streak = existing.getCurrentStreak() == null ? 0 : existing.getCurrentStreak();
            existing.setCurrentStreak(streak + 1);
            existing.setLastCompletedAt(today);
            return habitRepository.save(existing);
        }
        return existing;
    }

    /** Reverts today's completion; the streak never goes below zero. */
    @Transactional
    public Habit uncomplete(UUID currentUserId, Long habitId) {
        Habit existing = habitRepository.findById(habitId)
                .orElseThrow(() -> new NoSuchElementException("Habit not found with id: " + habitId));

        if (!existing.getUserId().equals(currentUserId)) {
            throw new ResourceOwnershipException("User does not own this habit");
        }

        LocalDate today = LocalDate.now();
        if (today.equals(existing.getLastCompletedAt())) {
            int streak = existing.getCurrentStreak() == null ? 0 : existing.getCurrentStreak();
            existing.setCurrentStreak(Math.max(0, streak - 1));
            existing.setLastCompletedAt(null);
            return habitRepository.save(existing);
        }
        return existing;
    }
}
