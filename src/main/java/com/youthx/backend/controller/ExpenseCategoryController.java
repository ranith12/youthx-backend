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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/expense-categories")
@RequiredArgsConstructor
public class ExpenseCategoryController {

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
        return response;
    }
}
