package com.youthx.backend.service;


import com.youthx.backend.entity.ExpenseCategory;
import com.youthx.backend.repository.ExpenseCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseCategoryService {

    private final ExpenseCategoryRepository expenseCategoryRepository;

    public List<ExpenseCategory> listAll() {
        return expenseCategoryRepository.findAll();
    }
}
