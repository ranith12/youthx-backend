package com.youthx.backend.service;

import com.youthx.backend.entity.Expense;
import com.youthx.backend.repository.ExpenseCategoryRepository;
import com.youthx.backend.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.youthx.backend.exception.ResourceOwnershipException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExpenseServiceTests {

    private ExpenseRepository expenseRepository;
    private ExpenseCategoryRepository expenseCategoryRepository;
    private ExpenseService expenseService;
    private final UUID owner = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        expenseRepository = mock(ExpenseRepository.class);
        expenseCategoryRepository = mock(ExpenseCategoryRepository.class);
        expenseService = new ExpenseService(expenseRepository, expenseCategoryRepository);
    }

    @Test
    void ownershipViolationOnUpdateThrowsIllegalArgumentException() {
        Expense expense = new Expense();
        expense.setId(1L);
        expense.setUserId(owner);

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        assertThrows(ResourceOwnershipException.class,
                () -> expenseService.update(UUID.randomUUID(), 1L, expense));
        verify(expenseRepository, never()).save(expense);
    }

    @Test
    void ownershipViolationOnDeleteThrowsIllegalArgumentException() {
        Expense expense = new Expense();
        expense.setId(1L);
        expense.setUserId(owner);

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        assertThrows(ResourceOwnershipException.class,
                () -> expenseService.delete(UUID.randomUUID(), 1L));
        verify(expenseRepository, never()).delete(expense);
    }

    @Test
    void ownerCanDelete() {
        Expense expense = new Expense();
        expense.setId(1L);
        expense.setUserId(owner);
        expense.setType("expense");
        expense.setAmount(new BigDecimal("5.00"));
        expense.setTransactionDate(LocalDate.now());

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        expenseService.delete(owner, 1L);
        verify(expenseRepository).delete(expense);
    }

    @Test
    void createWithNonexistentCategoryFailsBeforeSaveWithClientError() {
        Expense expense = new Expense();
        expense.setCategoryId(9999L);
        expense.setType("expense");
        expense.setAmount(new BigDecimal("5.00"));
        expense.setTransactionDate(LocalDate.now());

        when(expenseCategoryRepository.existsById(9999L)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> expenseService.create(owner, expense));

        org.junit.jupiter.api.Assertions.assertTrue(ex.getMessage().contains("9999"));
        verify(expenseRepository, never()).save(expense);
    }

    @Test
    void createWithExistingCategorySucceeds() {
        Expense expense = new Expense();
        expense.setCategoryId(7L);
        expense.setType("income");
        expense.setAmount(new BigDecimal("5.00"));
        expense.setTransactionDate(LocalDate.now());

        when(expenseCategoryRepository.existsById(7L)).thenReturn(true);
        when(expenseRepository.save(expense)).thenReturn(expense);

        expenseService.create(owner, expense);

        verify(expenseRepository).save(expense);
        org.junit.jupiter.api.Assertions.assertEquals(owner, expense.getUserId());
    }

    @Test
    void createWithoutCategoryIsAllowed() {
        Expense expense = new Expense();
        expense.setType("expense");
        expense.setAmount(new BigDecimal("5.00"));
        expense.setTransactionDate(LocalDate.now());

        when(expenseRepository.save(expense)).thenReturn(expense);

        expenseService.create(owner, expense);

        verify(expenseRepository).save(expense);
        verify(expenseCategoryRepository, never()).existsById(org.mockito.ArgumentMatchers.anyLong());
    }
}