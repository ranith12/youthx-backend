package com.youthx.backend.repository;


import com.youthx.backend.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findByUserId(UUID userId);
}
