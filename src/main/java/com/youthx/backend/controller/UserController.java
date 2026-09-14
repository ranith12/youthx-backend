package com.youthx.backend.controller;


import com.youthx.backend.dto.UserResponse;
import com.youthx.backend.entity.User;
import com.youthx.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping("/users/me")
    public ResponseEntity<UserResponse> me() {
        User user = userService.getById(currentUserProvider.currentUserId());

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setXpPoints(user.getXpPoints());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return ResponseEntity.ok(response);
    }
}
