package com.youthx.backend.controller;


import com.youthx.backend.dto.ExpenseCategoryResponse;
import com.youthx.backend.entity.ExpenseCategory;
import com.youthx.backend.service.ExpenseCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/expense-categories")
@RequiredArgsConstructor
public class ExpenseCategoryController {

    /**
     * Centralized income category name set.
     * Computed at runtime because the DB has no is_income column.
     * A category whose name appears in this set is treated as income.
     */
    private static final Set<String> INCOME_CATEGORY_NAMES = Set.of(
            "Salary", "Freelance", "Gift", "Investment", "Rental"
    );

    private final ExpenseCategoryService expenseCategoryService;

    @GetMapping
    public ResponseEntity<List<ExpenseCategoryResponse>> listAll() {
        List<ExpenseCategoryResponse> categories = expenseCategoryService.listAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    private ExpenseCategoryResponse toResponse(ExpenseCategory category) {
        ExpenseCategoryResponse response = new ExpenseCategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setIcon(category.getIcon());
        response.setIncome(INCOME_CATEGORY_NAMES.contains(category.getName()));
        return response;
    }
}
