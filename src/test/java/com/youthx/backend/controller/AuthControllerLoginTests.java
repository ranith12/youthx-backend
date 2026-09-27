package com.youthx.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youthx.backend.dto.LoginRequest;
import com.youthx.backend.entity.User;
import com.youthx.backend.exception.GlobalExceptionHandler;
import com.youthx.backend.repository.UserRepository;
import com.youthx.backend.security.JwtService;
import com.youthx.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerLoginTests {

    private static final String KNOWN_EMAIL = "known@example.com";

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final User knownUser = new User();

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);

        knownUser.setId(UUID.randomUUID());
        knownUser.setEmail(KNOWN_EMAIL);
        knownUser.setPasswordHash("hashed-secret");
        knownUser.setFullName("Known User");
        knownUser.setCreatedAt(OffsetDateTime.now());

        when(jwtService.generateToken(any(UUID.class))).thenReturn("issued-jwt-token");

        UserService userService = new UserService(userRepository, passwordEncoder);
        AuthController authController = new AuthController(jwtService, userService);

        this.mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private String loginBody(String email, String password) throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);
        return objectMapper.writeValueAsString(request);
    }

    @Test
    void unknownEmailWithPasswordReturns401() throws Exception {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("unknown@example.com", "some-password")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void unknownEmailDoesNotLeakExistenceInResponseBody() throws Exception {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        String body = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("unknown@example.com", "some-password")))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();

        org.junit.jupiter.api.Assertions.assertFalse(body.contains("unknown@example.com"),
                "Response must not echo the submitted email");
        org.junit.jupiter.api.Assertions.assertFalse(body.toLowerCase().contains("not found"),
                "Response must not reveal that the email is unregistered");
    }

    @Test
    void unknownEmailAndWrongPasswordReturnSameResponseAsWrongPassword() throws Exception {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail(KNOWN_EMAIL)).thenReturn(Optional.of(knownUser));
        when(passwordEncoder.matches("wrong-password", "hashed-secret")).thenReturn(false);

        String unknownBody = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("unknown@example.com", "wrong-password")))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        String wrongPasswordBody = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(KNOWN_EMAIL, "wrong-password")))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        org.junit.jupiter.api.Assertions.assertEquals(
                objectMapper.readTree(unknownBody).get("message"),
                objectMapper.readTree(wrongPasswordBody).get("message"),
                "Unknown email and wrong password must be indistinguishable");
    }

    @Test
    void wrongPasswordReturns401() throws Exception {
        when(userRepository.findByEmail(KNOWN_EMAIL)).thenReturn(Optional.of(knownUser));
        when(passwordEncoder.matches("wrong-password", "hashed-secret")).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(KNOWN_EMAIL, "wrong-password")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));

        verify(jwtService, never()).generateToken(any(UUID.class));
    }

    @Test
    void validLoginReturns200() throws Exception {
        when(userRepository.findByEmail(KNOWN_EMAIL)).thenReturn(Optional.of(knownUser));
        when(passwordEncoder.matches("correct-password", "hashed-secret")).thenReturn(true);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody(KNOWN_EMAIL, "correct-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("issued-jwt-token"))
                .andExpect(jsonPath("$.user.email").value(KNOWN_EMAIL));
    }

    @Test
    void malformedLoginBodyReturns400() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }
}
