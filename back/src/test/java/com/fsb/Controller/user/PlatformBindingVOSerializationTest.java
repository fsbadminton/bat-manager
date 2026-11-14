package com.fsb.Controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fsb.pojo.VO.PlatformBindingVO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlatformBindingVOSerializationTest {
    @Test
    void bindingResponseExposesOpaqueReferenceButNotRawExternalUserId() throws Exception {
        PlatformBindingVO binding = new PlatformBindingVO();
        binding.setPlatform("telegram");
        binding.setBindingReference("opaque-hmac-reference");
        binding.setExternalUserId("raw-telegram-user-id");

        String json = new ObjectMapper().writeValueAsString(binding);

        assertTrue(json.contains("opaque-hmac-reference"));
        assertFalse(json.contains("raw-telegram-user-id"));
        assertFalse(json.contains("externalUserId"));
    }
}
