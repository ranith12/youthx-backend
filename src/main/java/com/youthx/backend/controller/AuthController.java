package com.youthx.backend.controller;


import com.youthx.backend.dto.LoginRequest;
import com.youthx.backend.dto.RegisterRequest;
import com.youthx.backend.dto.UserResponse;
import com.youthx.backend.entity.User;
import com.youthx.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.youthx.backend.dto.LoginResponse;
import com.youthx.backend.security.JwtService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request.getEmail(), request.getPassword(), request.getFullName());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(user));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.login(request.getEmail(), request.getPassword());

        String token = jwtService.generateToken(user.getId());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(toResponse(user));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Logout server-side behavior remains OPEN.
        return ResponseEntity.ok().build();
    }

    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setXpPoints(user.getXpPoints());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
