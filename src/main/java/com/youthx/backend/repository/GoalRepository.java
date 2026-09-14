package com.youthx.backend.repository;


import com.youthx.backend.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByUserId(UUID userId);
}
