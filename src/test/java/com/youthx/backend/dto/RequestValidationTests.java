package com.youthx.backend.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestValidationTests {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    private Set<ConstraintViolation<Object>> validate(Object request) {
        return validator.validate(request);
    }

    private static boolean hasViolationOn(Set<ConstraintViolation<Object>> violations, String field) {
        return violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals(field));
    }

    private static CreateExpenseRequest validExpense() {
        CreateExpenseRequest request = new CreateExpenseRequest();
        request.setCategoryId(1L);
        request.setType("expense");
        request.setAmount(new BigDecimal("10.00"));
        request.setTransactionDate(LocalDate.now());
        return request;
    }

    private static DepositRequest validDeposit() {
        DepositRequest request = new DepositRequest();
        request.setAmount(new BigDecimal("10.00"));
        return request;
    }

    private static UpdateTodoRequest validTodo() {
        UpdateTodoRequest request = new UpdateTodoRequest();
        request.setTitle("Ship it");
        request.setPriority("medium");
        request.setIsCompleted(false);
        return request;
    }

    private static UpdateExpenseRequest validExpenseUpdate() {
        UpdateExpenseRequest request = new UpdateExpenseRequest();
        request.setCategoryId(1L);
        request.setType("expense");
        request.setAmount(new BigDecimal("10.00"));
        request.setTransactionDate(LocalDate.now());
        return request;
    }

    private static CreateTodoRequest validTodoCreate() {
        CreateTodoRequest request = new CreateTodoRequest();
        request.setTitle("Ship it");
        request.setPriority("medium");
        request.setIsCompleted(false);
        return request;
    }

    // ---------- CreateExpenseRequest ----------

    @Test
    void expenseRequestWithValidValuesPasses() {
        assertTrue(validate(validExpense()).isEmpty());
    }

    @Test
    void expenseRequestWithNullAmountFails() {
        CreateExpenseRequest request = validExpense();
        request.setAmount(null);

        Set<ConstraintViolation<Object>> violations = validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(hasViolationOn(violations, "amount"));
    }

    @Test
    void expenseRequestWithNullTypeFails() {
        CreateExpenseRequest request = validExpense();
        request.setType(null);

        Set<ConstraintViolation<Object>> violations = validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(hasViolationOn(violations, "type"));
    }

    @Test
    void expenseRequestWithInvalidTypeFails() {
        for (String invalidType : new String[]{"transfer", "EXPENSE", "expenses", "", "expense "}) {
            CreateExpenseRequest request = validExpense();
            request.setType(invalidType);

            Set<ConstraintViolation<Object>> violations = validate(request);

            assertFalse(violations.isEmpty(), "type '" + invalidType + "' must be rejected");
            assertTrue(hasViolationOn(violations, "type"));
        }
    }

    @Test
    void expenseRequestAcceptsBothValidTypes() {
        for (String validType : new String[]{"expense", "income"}) {
            CreateExpenseRequest request = validExpense();
            request.setType(validType);

            assertTrue(validate(request).isEmpty(), "type '" + validType + "' must be accepted");
        }
    }

    @Test
    void expenseRequestStillRejectsNonPositiveAmount() {
        CreateExpenseRequest request = validExpense();
        request.setAmount(new BigDecimal("-1.00"));

        assertTrue(hasViolationOn(validate(request), "amount"));

        request.setAmount(BigDecimal.ZERO);
        assertTrue(hasViolationOn(validate(request), "amount"));
    }

    // ---------- DepositRequest ----------

    @Test
    void depositRequestWithValidAmountPasses() {
        assertTrue(validate(validDeposit()).isEmpty());
    }

    @Test
    void depositRequestWithNullAmountFails() {
        DepositRequest request = validDeposit();
        request.setAmount(null);

        Set<ConstraintViolation<Object>> violations = validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(hasViolationOn(violations, "amount"));
    }

    @Test
    void depositRequestStillRejectsNonPositiveAmount() {
        DepositRequest request = validDeposit();
        request.setAmount(new BigDecimal("0.00"));

        assertTrue(hasViolationOn(validate(request), "amount"));
    }

    // ---------- UpdateTodoRequest ----------

    @Test
    void todoRequestWithValidValuesPasses() {
        assertTrue(validate(validTodo()).isEmpty());
    }

    @Test
    void todoRequestWithNullPriorityFails() {
        UpdateTodoRequest request = validTodo();
        request.setPriority(null);

        Set<ConstraintViolation<Object>> violations = validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(hasViolationOn(violations, "priority"));
    }

    @Test
    void todoRequestWithNullIsCompletedFails() {
        UpdateTodoRequest request = validTodo();
        request.setIsCompleted(null);

        Set<ConstraintViolation<Object>> violations = validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(hasViolationOn(violations, "isCompleted"));
    }

    @Test
    void todoRequestStillRejectsBlankTitle() {
        UpdateTodoRequest request = validTodo();
        request.setTitle("  ");

        assertTrue(hasViolationOn(validate(request), "title"));
    }

    @Test
    void todoRequestPreservesExistingValidPriorities() {
        for (String priority : new String[]{"high", "medium", "low"}) {
            UpdateTodoRequest request = validTodo();
            request.setPriority(priority);

            assertTrue(validate(request).isEmpty(), "priority '" + priority + "' must remain accepted");
        }
    }

    @Test
    void todoRequestNullDueDateRemainsOptional() {
        UpdateTodoRequest request = validTodo();
        request.setDueDate(null);

        Set<ConstraintViolation<Object>> violations = validate(request);

        assertEquals(0, violations.stream().filter(v -> v.getPropertyPath().toString().equals("dueDate")).count());
    }

    @Test
    void todoRequestRejectsInvalidPriority() {
        for (String invalidPriority : new String[]{"urgent", "HIGH", "Medium", "", "low "}) {
            UpdateTodoRequest request = validTodo();
            request.setPriority(invalidPriority);

            Set<ConstraintViolation<Object>> violations = validate(request);

            assertFalse(violations.isEmpty(), "priority '" + invalidPriority + "' must be rejected");
            assertTrue(hasViolationOn(violations, "priority"));
        }
    }

    // ---------- UpdateExpenseRequest ----------

    @Test
    void expenseUpdateRequestWithValidValuesPasses() {
        assertTrue(validate(validExpenseUpdate()).isEmpty());
    }

    @Test
    void expenseUpdateRequestRejectsInvalidType() {
        for (String invalidType : new String[]{"transfer", "EXPENSE", "expenses", "", "expense "}) {
            UpdateExpenseRequest request = validExpenseUpdate();
            request.setType(invalidType);

            Set<ConstraintViolation<Object>> violations = validate(request);

            assertFalse(violations.isEmpty(), "type '" + invalidType + "' must be rejected");
            assertTrue(hasViolationOn(violations, "type"));
        }
    }

    @Test
    void expenseUpdateRequestAcceptsBothValidTypes() {
        for (String validType : new String[]{"expense", "income"}) {
            UpdateExpenseRequest request = validExpenseUpdate();
            request.setType(validType);

            assertTrue(validate(request).isEmpty(), "type '" + validType + "' must be accepted");
        }
    }

    @Test
    void expenseUpdateRequestWithNullTypeFails() {
        UpdateExpenseRequest request = validExpenseUpdate();
        request.setType(null);

        assertTrue(hasViolationOn(validate(request), "type"));
    }

    // ---------- CreateTodoRequest ----------

    @Test
    void todoCreateRequestRejectsInvalidPriority() {
        for (String invalidPriority : new String[]{"urgent", "HIGH", "Medium", "", "low "}) {
            CreateTodoRequest request = validTodoCreate();
            request.setPriority(invalidPriority);

            Set<ConstraintViolation<Object>> violations = validate(request);

            assertFalse(violations.isEmpty(), "priority '" + invalidPriority + "' must be rejected");
            assertTrue(hasViolationOn(violations, "priority"));
        }
    }

    @Test
    void todoCreateRequestAcceptsAllValidPriorities() {
        for (String priority : new String[]{"high", "medium", "low"}) {
            CreateTodoRequest request = validTodoCreate();
            request.setPriority(priority);

            assertTrue(validate(request).isEmpty(), "priority '" + priority + "' must be accepted");
        }
    }

    @Test
    void todoCreateRequestStillAllowsOmittedPriority() {
        CreateTodoRequest request = validTodoCreate();
        request.setPriority(null);

        assertTrue(validate(request).isEmpty(), "omitted priority must fall through to the service default");

        CreateTodoRequest bare = new CreateTodoRequest();
        bare.setTitle("Only a title");
        assertTrue(validate(bare).isEmpty(), "title-only payload must remain valid");
    }
}
