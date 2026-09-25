package com.youthx.backend.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExpenseCategoryResponseJsonTest {

    @Test
    void testSerializationOnlyHasIsIncome() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ExpenseCategoryResponse dto = new ExpenseCategoryResponse();
        dto.setId(1L);
        dto.setName("Food");
        dto.setIcon("🍔");
        dto.setIncome(false);

        String json = mapper.writeValueAsString(dto);
        System.out.println("Serialized JSON: " + json);

        assertTrue(json.contains("\"isIncome\":false"), "JSON should contain isIncome");
        assertFalse(json.contains("\"income\":"), "JSON should NOT contain income");
    }
}
