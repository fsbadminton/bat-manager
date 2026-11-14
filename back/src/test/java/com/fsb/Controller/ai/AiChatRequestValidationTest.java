package com.fsb.Controller.ai;

import com.fsb.pojo.DTO.AiChatRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiChatRequestValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void acceptsBoundedChatRequest() {
        AiChatRequest request = new AiChatRequest();
        request.setConversationId("web-user_1-abc123");
        request.setMessage("查询我的订单");

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void rejectsOversizedMessageAndUnsafeConversationId() {
        AiChatRequest request = new AiChatRequest();
        request.setConversationId("../other-session");
        request.setMessage("x".repeat(5001));

        assertEquals(2, validator.validate(request).size());
    }
}
