package com.fsb.Controller.admin;

import com.fsb.Mapper.IdentityBindingAuditMapper;
import com.fsb.Service.PlatformIdentityService;
import com.fsb.auth.AuthContext;
import com.fsb.auth.AuthUser;
import com.fsb.pojo.DTO.AdminIdentityUnbindRequest;
import com.fsb.pojo.entity.IdentityBindingAudit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IdentityBindingAdminControllerTest {
    @AfterEach
    void clearAuthContext() {
        AuthContext.clear();
    }

    @Test
    void adminUnbindAuditsOpaqueReferenceAndOutcome() {
        PlatformIdentityService identities = mock(PlatformIdentityService.class);
        IdentityBindingAuditMapper audits = mock(IdentityBindingAuditMapper.class);
        doAnswer(invocation -> {
            IdentityBindingAudit row = invocation.getArgument(0);
            row.setId(7L);
            return null;
        }).when(audits).insert(any(IdentityBindingAudit.class));
        AuthContext.setCurrentUser(AuthUser.builder().username("operator").role("ADMIN").build());
        IdentityBindingAdminController controller = new IdentityBindingAdminController(identities, audits);
        AdminIdentityUnbindRequest request = new AdminIdentityUnbindRequest();
        request.setPlatform("telegram");
        request.setBindingReference("opaque-hmac-reference");
        request.setReason("account recovery");

        assertNotNull(controller.unbind(42L, request));

        verify(identities).unbindByReference(42L, "telegram", "opaque-hmac-reference");
        verify(audits).updateOutcome(7L, "SUCCEEDED");
        verify(audits).insert(org.mockito.ArgumentMatchers.argThat(row ->
                "opaque-hmac-reference".equals(row.getExternalUserIdHash())
                        && "operator".equals(row.getOperatorName())
                        && "PENDING".equals(row.getOutcome())));
    }

    @Test
    void adminUnbindRecordsFailureOutcome() {
        PlatformIdentityService identities = mock(PlatformIdentityService.class);
        IdentityBindingAuditMapper audits = mock(IdentityBindingAuditMapper.class);
        doAnswer(invocation -> {
            IdentityBindingAudit row = invocation.getArgument(0);
            row.setId(8L);
            return null;
        }).when(audits).insert(any(IdentityBindingAudit.class));
        when(audits.list(null)).thenReturn(java.util.List.of());
        org.mockito.Mockito.doThrow(new IllegalStateException("mapping unavailable"))
                .when(identities).unbindByReference(42L, "qq", "opaque-reference");
        AuthContext.setCurrentUser(AuthUser.builder().username("operator").role("ADMIN").build());
        IdentityBindingAdminController controller = new IdentityBindingAdminController(identities, audits);
        AdminIdentityUnbindRequest request = new AdminIdentityUnbindRequest();
        request.setPlatform("qq");
        request.setBindingReference("opaque-reference");
        request.setReason("duplicate identity");

        assertThrows(IllegalStateException.class, () -> controller.unbind(42L, request));

        verify(audits).updateOutcome(8L, "FAILED");
    }
}
