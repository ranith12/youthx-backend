package com.youthx.backend.service;


import com.youthx.backend.entity.Todo;
import com.youthx.backend.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    public Todo create(UUID currentUserId, Todo todo) {
        todo.setUserId(currentUserId);
        todo.setCreatedAt(OffsetDateTime.now());
        if (todo.getPriority() == null) {
            todo.setPriority("medium");
        }
        if (todo.getIsCompleted() == null) {
            todo.setIsCompleted(false);
        }
        return todoRepository.save(todo);
    }

    public List<Todo> listByUser(UUID currentUserId) {
        return todoRepository.findByUserId(currentUserId);
    }

    public Todo update(UUID currentUserId, Long todoId, Todo todo) {
        Todo existing = todoRepository.findById(todoId)
                .orElseThrow(() -> new NoSuchElementException("Todo not found with id: " + todoId));

        if (!existing.getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException("User does not own this todo");
        }

        existing.setTitle(todo.getTitle());
        existing.setPriority(todo.getPriority());
        existing.setDueDate(todo.getDueDate());
        existing.setIsCompleted(todo.getIsCompleted());
        existing.setUpdatedAt(OffsetDateTime.now());

        return todoRepository.save(existing);
    }

    public void delete(UUID currentUserId, Long todoId) {
        Todo existing = todoRepository.findById(todoId)
                .orElseThrow(() -> new NoSuchElementException("Todo not found with id: " + todoId));

        if (!existing.getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException("User does not own this todo");
        }

        todoRepository.delete(existing);
    }
}
