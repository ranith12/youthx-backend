package com.youthx.backend.controller;


import com.youthx.backend.dto.CreateExpenseRequest;
import com.youthx.backend.dto.ExpenseResponse;
import com.youthx.backend.dto.PageResponse;
import com.youthx.backend.entity.Expense;
import com.youthx.backend.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.youthx.backend.dto.UpdateExpenseRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody CreateExpenseRequest request) {
        UUID currentUserId = currentUserProvider.currentUserId();

        Expense expense = new Expense();
        expense.setCategoryId(request.getCategoryId());
        expense.setType(request.getType());
        expense.setAmount(request.getAmount());
        expense.setNote(request.getNote());
        expense.setTransactionDate(request.getTransactionDate());

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(expenseService.create(currentUserId, expense)));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ExpenseResponse>> list(Pageable pageable) {
        UUID currentUserId = currentUserProvider.currentUserId();
        Page<Expense> page = expenseService.listByUser(currentUserId, pageable);

        List<ExpenseResponse> content = page.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new PageResponse<>(content, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateExpenseRequest request) {

        UUID currentUserId = currentUserProvider.currentUserId();

        Expense expense = new Expense();
        expense.setCategoryId(request.getCategoryId());
        expense.setType(request.getType());
        expense.setAmount(request.getAmount());
        expense.setNote(request.getNote());
        expense.setTransactionDate(request.getTransactionDate());

        Expense updatedExpense = expenseService.update(currentUserId, id, expense);

        return ResponseEntity.ok(toResponse(updatedExpense));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        UUID currentUserId = currentUserProvider.currentUserId();

        expenseService.delete(currentUserId, id);

        return ResponseEntity.noContent().build();
    }

    private ExpenseResponse toResponse(Expense expense) {
        ExpenseResponse response = new ExpenseResponse();
        response.setId(expense.getId());
        response.setUserId(expense.getUserId());
        response.setCategoryId(expense.getCategoryId());
        response.setType(expense.getType());
        response.setAmount(expense.getAmount());
        response.setNote(expense.getNote());
        response.setTransactionDate(expense.getTransactionDate());
        response.setCreatedAt(expense.getCreatedAt());
        return response;
    }
}
