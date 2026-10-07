package com.fsb.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RolePermissionServiceTest {

    private final RolePermissionService service = new RolePermissionService();

    @Test
    void adminPermissionsDoNotIncludeUserOnlyPermissions() {
        var permissions = service.resolvePermissions("admin");

        assertTrue(permissions.contains(PermissionConstants.ADMIN_ACCESS));
        assertTrue(permissions.contains(PermissionConstants.ORDER_READ_ANY));
        assertFalse(permissions.contains(PermissionConstants.ORDER_CREATE_OWN));
    }

    @Test
    void userPermissionsCannotReadOtherUsersReviews() {
        var permissions = service.resolvePermissions("user");

        assertTrue(permissions.contains(PermissionConstants.REVIEW_READ_OWN));
        assertFalse(permissions.contains(PermissionConstants.REVIEW_READ_ANY));
        assertFalse(permissions.contains(PermissionConstants.ADMIN_ACCESS));
    }
}
