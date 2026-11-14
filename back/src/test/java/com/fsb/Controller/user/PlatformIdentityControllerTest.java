package com.fsb.Controller.user;

import com.fsb.Service.PlatformIdentityService;
import com.fsb.auth.AuthContext;
import com.fsb.auth.AuthUser;
import com.fsb.pojo.DTO.PlatformBindingUnbindRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PlatformIdentityControllerTest {
    @AfterEach
    void clearAuthContext() {
        AuthContext.clear();
    }

    @Test
    void unbindUsesAuthenticatedCustomerAndRequestBodyIdentity() {
        PlatformIdentityService identities = mock(PlatformIdentityService.class);
        PlatformIdentityController controller = new PlatformIdentityController(identities);
        AuthContext.setCurrentUser(AuthUser.builder().userId(42L).username("alice").role("USER").build());
        PlatformBindingUnbindRequest request = new PlatformBindingUnbindRequest();
        request.setPlatform("telegram");
        request.setBindingReference("binding-ref-7");

        assertNotNull(controller.unbind(request));

        verify(identities).unbindByReference(42L, "telegram", "binding-ref-7");
    }
}
