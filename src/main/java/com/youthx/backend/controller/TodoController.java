package com.youthx.backend.controller;


import com.youthx.backend.dto.CreateTodoRequest;
import com.youthx.backend.dto.TodoResponse;
import com.youthx.backend.dto.UpdateTodoRequest;
import com.youthx.backend.entity.Todo;
import com.youthx.backend.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    public ResponseEntity<TodoResponse> create(@Valid @RequestBody CreateTodoRequest request) {
        UUID currentUserId = currentUserProvider.currentUserId();

        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setPriority(request.getPriority());
        todo.setDueDate(request.getDueDate());
        todo.setIsCompleted(request.getIsCompleted());

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(todoService.create(currentUserId, todo)));
    }

    @GetMapping
    public ResponseEntity<List<TodoResponse>> list() {
        UUID currentUserId = currentUserProvider.currentUserId();
        List<TodoResponse> todos = todoService.listByUser(currentUserId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(todos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UpdateTodoRequest request) {
        UUID currentUserId = currentUserProvider.currentUserId();

        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setPriority(request.getPriority());
        todo.setDueDate(request.getDueDate());
        todo.setIsCompleted(request.getIsCompleted());

        return ResponseEntity.ok(toResponse(todoService.update(currentUserId, id, todo)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        UUID currentUserId = currentUserProvider.currentUserId();
        todoService.delete(currentUserId, id);
        return ResponseEntity.noContent().build();
    }

    private TodoResponse toResponse(Todo todo) {
        TodoResponse response = new TodoResponse();
        response.setId(todo.getId());
        response.setUserId(todo.getUserId());
        response.setTitle(todo.getTitle());
        response.setPriority(todo.getPriority());
        response.setDueDate(todo.getDueDate());
        response.setIsCompleted(todo.getIsCompleted());
        response.setCreatedAt(todo.getCreatedAt());
        response.setUpdatedAt(todo.getUpdatedAt());
        return response;
    }
}
