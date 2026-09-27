package com.youthx.backend.repository;


import com.youthx.backend.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    Page<Expense> findByUserId(UUID userId, Pageable pageable);

    Page<Expense> findByUserIdOrderByTransactionDateDescIdDesc(UUID userId, Pageable pageable);
}
