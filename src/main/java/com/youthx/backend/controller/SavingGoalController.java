package com.youthx.backend.controller;

import com.youthx.backend.dto.CreateSavingGoalRequest;
import com.youthx.backend.dto.DepositRequest;
import com.youthx.backend.dto.SavingGoalResponse;
import com.youthx.backend.dto.UpdateSavingGoalRequest;
import com.youthx.backend.entity.SavingGoal;
import com.youthx.backend.service.SavingGoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/saving-goals")
@RequiredArgsConstructor
public class SavingGoalController {

    private final SavingGoalService savingGoalService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    public ResponseEntity<SavingGoalResponse> create(
            @Valid @RequestBody CreateSavingGoalRequest request) {

        UUID currentUserId = currentUserProvider.currentUserId();

        SavingGoal savingGoal = new SavingGoal();
        savingGoal.setName(request.getName());
        savingGoal.setTargetAmount(request.getTargetAmount());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(
                        savingGoalService.create(currentUserId, savingGoal)
                ));
    }

    @GetMapping
    public ResponseEntity<List<SavingGoalResponse>> list() {

        UUID currentUserId = currentUserProvider.currentUserId();

        List<SavingGoalResponse> goals =
                savingGoalService.listByUser(currentUserId)
                        .stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(goals);
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<SavingGoalResponse> deposit(
            @PathVariable Long id,
            @Valid @RequestBody DepositRequest request) {

        UUID currentUserId = currentUserProvider.currentUserId();

        SavingGoal updated =
                savingGoalService.deposit(
                        currentUserId,
                        id,
                        request.getAmount()
                );

        return ResponseEntity.ok(toResponse(updated));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavingGoalResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSavingGoalRequest request) {

        UUID currentUserId = currentUserProvider.currentUserId();

        SavingGoal savingGoal = new SavingGoal();
        savingGoal.setName(request.getName());
        savingGoal.setTargetAmount(request.getTargetAmount());

        SavingGoal updatedSavingGoal =
                savingGoalService.update(
                        currentUserId,
                        id,
                        savingGoal
                );

        return ResponseEntity.ok(toResponse(updatedSavingGoal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        UUID currentUserId = currentUserProvider.currentUserId();

        savingGoalService.delete(currentUserId, id);

        return ResponseEntity.noContent().build();
    }

    private SavingGoalResponse toResponse(SavingGoal savingGoal) {

        SavingGoalResponse response = new SavingGoalResponse();

        response.setId(savingGoal.getId());
        response.setUserId(savingGoal.getUserId());
        response.setName(savingGoal.getName());
        response.setTargetAmount(savingGoal.getTargetAmount());
        response.setCurrentAmount(savingGoal.getCurrentAmount());
        response.setCreatedAt(savingGoal.getCreatedAt());

        BigDecimal currentAmount =
                savingGoal.getCurrentAmount() == null
                        ? BigDecimal.ZERO
                        : savingGoal.getCurrentAmount();

        BigDecimal percent =
                currentAmount
                        .multiply(BigDecimal.valueOf(100))
                        .divide(
                                savingGoal.getTargetAmount(),
                                2,
                                RoundingMode.HALF_UP
                        );

        response.setProgressPercent(percent.intValue());

        return response;
    }
}