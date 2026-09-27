package com.youthx.backend.exception;

import com.youthx.backend.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTests {

    private GlobalExceptionHandler handler;
    private WebRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new ServletWebRequest(new MockHttpServletRequest("POST", "/api/expenses"));
    }

    @Test
    void dataIntegrityViolationReturns400WithErrorResponseShape() {
        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolation(
                new DataIntegrityViolationException("ERROR: new row for relation violates check constraint \"chk_expenses_type\""),
                request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getStatusCode().value());

        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertEquals("Bad Request", body.getError());
        assertNotNull(body.getTimestamp());
        assertNotNull(body.getPath());
    }

    @Test
    void dataIntegrityViolationDoesNotLeakDatabaseDetails() {
        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolation(
                new DataIntegrityViolationException("FATAL: insert into expenses failed, relation \"users\""),
                request);

        String message = response.getBody().getMessage();
        assertFalse(message.contains("expenses"), "message must not expose table names");
        assertFalse(message.contains("FATAL"), "message must not expose database error text");
        assertFalse(message.toLowerCase().contains("relation"), "message must not expose database error text");
    }

    @Test
    void unreadableMessageBodyReturns400WithErrorResponseShape() {
        ResponseEntity<ErrorResponse> response = handler.handleMessageNotReadable(
                new HttpMessageNotReadableException("Unexpected end-of-input", (HttpInputMessage) null),
                request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertEquals("Bad Request", body.getError());
        assertNotNull(body.getMessage());
        assertFalse(body.getMessage().contains("Unexpected end-of-input"),
                "message must not expose parser internals");
    }

    @Test
    void existingMappingsAreUnchanged() {
        assertEquals(HttpStatus.UNAUTHORIZED,
                handler.handleUnauthorized(new InvalidCredentialsException("Invalid credentials"), request)
                        .getStatusCode());

        assertEquals(HttpStatus.NOT_FOUND,
                handler.handleNotFound(new java.util.NoSuchElementException("Expense not found"), request)
                        .getStatusCode());

        assertEquals(HttpStatus.BAD_REQUEST,
                handler.handleBadRequest(new IllegalArgumentException("Expense category not found: 9999"), request)
                        .getStatusCode());

        assertEquals(HttpStatus.FORBIDDEN,
                handler.handleForbidden(new ResourceOwnershipException("You do not own this expense"), request)
                        .getStatusCode());

        assertEquals(HttpStatus.CONFLICT,
                handler.handleConflict(new DuplicateResourceException("Email is already registered"), request)
                        .getStatusCode());
    }
}
