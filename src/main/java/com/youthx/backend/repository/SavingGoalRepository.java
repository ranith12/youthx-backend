package com.youthx.backend.repository;


import com.youthx.backend.entity.SavingGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SavingGoalRepository extends JpaRepository<SavingGoal, Long> {

    List<SavingGoal> findByUserId(UUID userId);
}
