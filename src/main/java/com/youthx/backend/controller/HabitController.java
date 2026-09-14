package com.youthx.backend.controller;


import com.youthx.backend.dto.CreateHabitRequest;
import com.youthx.backend.dto.HabitResponse;
import com.youthx.backend.dto.UpdateHabitRequest;
import com.youthx.backend.entity.Habit;
import com.youthx.backend.service.HabitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/habits")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService habitService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    public ResponseEntity<HabitResponse> create(@Valid @RequestBody CreateHabitRequest request) {
        UUID currentUserId = currentUserProvider.currentUserId();

        Habit habit = new Habit();
        habit.setName(request.getName());
        habit.setEmoji(request.getEmoji());
        habit.setColor(request.getColor());
        habit.setFrequency(request.getFrequency());
        habit.setTimeOfDay(request.getTimeOfDay());

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(habitService.create(currentUserId, habit)));
    }

    @GetMapping
    public ResponseEntity<List<HabitResponse>> list() {
        UUID currentUserId = currentUserProvider.currentUserId();
        List<HabitResponse> habits = habitService.listByUser(currentUserId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(habits);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabitResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody UpdateHabitRequest request) {
        UUID currentUserId = currentUserProvider.currentUserId();

        Habit habit = new Habit();
        habit.setName(request.getName());
        habit.setEmoji(request.getEmoji());
        habit.setColor(request.getColor());
        habit.setFrequency(request.getFrequency());
        habit.setTimeOfDay(request.getTimeOfDay());

        return ResponseEntity.ok(toResponse(habitService.update(currentUserId, id, habit)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        UUID currentUserId = currentUserProvider.currentUserId();
        habitService.delete(currentUserId, id);
        return ResponseEntity.noContent().build();
    }

    private HabitResponse toResponse(Habit habit) {
        HabitResponse response = new HabitResponse();
        response.setId(habit.getId());
        response.setUserId(habit.getUserId());
        response.setName(habit.getName());
        response.setEmoji(habit.getEmoji());
        response.setColor(habit.getColor());
        response.setFrequency(habit.getFrequency());
        response.setTimeOfDay(habit.getTimeOfDay());
        response.setCurrentStreak(habit.getCurrentStreak());
        response.setLastCompletedAt(habit.getLastCompletedAt());
        return response;
    }
}
