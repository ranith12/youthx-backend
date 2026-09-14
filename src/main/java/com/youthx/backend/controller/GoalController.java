package com.youthx.backend.controller;


import com.youthx.backend.dto.CreateGoalRequest;
import com.youthx.backend.dto.GoalResponse;
import com.youthx.backend.dto.UpdateGoalRequest;
import com.youthx.backend.entity.Goal;
import com.youthx.backend.service.GoalService;
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
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    public ResponseEntity<GoalResponse> create(@Valid @RequestBody CreateGoalRequest request) {
        UUID currentUserId = currentUserProvider.currentUserId();

        Goal goal = new Goal();
        goal.setName(request.getName());
        goal.setCategory(request.getCategory());
        goal.setTargetDate(request.getTargetDate());
        goal.setProgressPercent(request.getProgressPercent());
        goal.setDailyReminderTime(request.getDailyReminderTime());

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(goalService.create(currentUserId, goal)));
    }

    @GetMapping
    public ResponseEntity<List<GoalResponse>> list() {
        UUID currentUserId = currentUserProvider.currentUserId();
        List<GoalResponse> goals = goalService.listByUser(currentUserId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalResponse> getById(@PathVariable Long id) {
        UUID currentUserId = currentUserProvider.currentUserId();

        Goal goal = goalService.getById(currentUserId, id);

        return ResponseEntity.ok(toResponse(goal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UpdateGoalRequest request) {
        UUID currentUserId = currentUserProvider.currentUserId();

        Goal goal = new Goal();
        goal.setName(request.getName());
        goal.setCategory(request.getCategory());
        goal.setTargetDate(request.getTargetDate());
        goal.setProgressPercent(request.getProgressPercent());
        goal.setDailyReminderTime(request.getDailyReminderTime());

        return ResponseEntity.ok(toResponse(goalService.update(currentUserId, id, goal)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        UUID currentUserId = currentUserProvider.currentUserId();
        goalService.delete(currentUserId, id);
        return ResponseEntity.noContent().build();
    }

    private GoalResponse toResponse(Goal goal) {
        GoalResponse response = new GoalResponse();
        response.setId(goal.getId());
        response.setUserId(goal.getUserId());
        response.setName(goal.getName());
        response.setCategory(goal.getCategory());
        response.setTargetDate(goal.getTargetDate());
        response.setProgressPercent(goal.getProgressPercent());
        response.setDailyReminderTime(goal.getDailyReminderTime());
        return response;
    }
}
