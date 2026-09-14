package com.youthx.backend.service;


import com.youthx.backend.entity.Expense;
import com.youthx.backend.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public Expense create(UUID currentUserId, Expense expense) {
        expense.setUserId(currentUserId);
        expense.setCreatedAt(OffsetDateTime.now());
        return expenseRepository.save(expense);
    }

    public Page<Expense> listByUser(UUID currentUserId, Pageable pageable) {
        return expenseRepository.findByUserId(currentUserId, pageable);
    }

    public Expense update(UUID currentUserId, Long expenseId, Expense updatedExpense) {
        Expense existingExpense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new NoSuchElementException("Expense not found"));

        if (!existingExpense.getUserId().equals(currentUserId)) {
            throw new IllegalStateException("You do not own this expense");
        }

        existingExpense.setCategoryId(updatedExpense.getCategoryId());
        existingExpense.setType(updatedExpense.getType());
        existingExpense.setAmount(updatedExpense.getAmount());
        existingExpense.setNote(updatedExpense.getNote());
        existingExpense.setTransactionDate(updatedExpense.getTransactionDate());

        return expenseRepository.save(existingExpense);
    }

    public void delete(UUID currentUserId, Long expenseId) {
        Expense existingExpense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new NoSuchElementException("Expense not found"));

        if (!existingExpense.getUserId().equals(currentUserId)) {
            throw new IllegalStateException("You do not own this expense");
        }

        expenseRepository.delete(existingExpense);
    }
}
