package com.fsb.Controller.ai;

import com.fsb.Mapper.UserMapper;
import com.fsb.Service.PlatformIdentityService;
import com.fsb.auth.AiContextSignature;
import com.fsb.auth.AiRequestReplayGuard;
import com.fsb.pojo.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AiIdentityControllerTest {
    @Test
    void webIdentityStatusVerifiesSignatureConsumesNonceAndReturnsBackendUsername() {
        PlatformIdentityService identities = mock(PlatformIdentityService.class);
        AiContextSignature signatures = mock(AiContextSignature.class);
        AiRequestReplayGuard replayGuard = mock(AiRequestReplayGuard.class);
        UserMapper users = mock(UserMapper.class);
        AiIdentityController controller = new AiIdentityController(identities, signatures, replayGuard, users);
        User user = new User();
        user.setId(42L);
        user.setUsername("trusted-user");
        user.setStatus(1);
        when(users.getById(42L)).thenReturn(user);

        var result = controller.webStatus("42", "timestamp", "nonce", "signature");

        verify(signatures).verifyWebUser("42", "timestamp", "nonce", "signature");
        verify(replayGuard).consume("nonce");
        assertEquals("trusted-user", result.getData().get("username"));
        assertEquals(42L, result.getData().get("userId"));
    }
}
