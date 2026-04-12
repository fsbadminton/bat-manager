package com.fsb.auth;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Component
public class RolePermissionService {

    public List<String> resolvePermissions(String role) {
        String normalizedRole = normalizeRole(role);
        if ("ADMIN".equals(normalizedRole)) {
            List<String> permissions = new ArrayList<>();
            permissions.add(PermissionConstants.ADMIN_ACCESS);
            permissions.add(PermissionConstants.REVIEW_READ_ANY);
            permissions.add(PermissionConstants.ORDER_READ_ANY);
            permissions.add(PermissionConstants.ORDER_UPDATE_ANY);
            permissions.add(PermissionConstants.PRODUCT_UPDATE_ANY);
            permissions.add(PermissionConstants.REFUND_APPROVE_ANY);
            return permissions;
        }

        List<String> permissions = new ArrayList<>();
        permissions.add(PermissionConstants.REVIEW_CREATE_OWN);
        permissions.add(PermissionConstants.REVIEW_READ_OWN);
        permissions.add(PermissionConstants.REVIEW_UPDATE_OWN);
        permissions.add(PermissionConstants.REVIEW_DELETE_OWN);
        permissions.add(PermissionConstants.ORDER_CREATE_OWN);
        permissions.add(PermissionConstants.ORDER_READ_OWN);
        permissions.add(PermissionConstants.ORDER_UPDATE_OWN);
        permissions.add(PermissionConstants.REFUND_APPLY_OWN);
        permissions.add(PermissionConstants.REFUND_READ_OWN);
        return permissions;
    }

    public String normalizeRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return "USER";
        }
        return role.trim().toUpperCase(Locale.ROOT);
    }

    public List<String> immutablePermissions(List<String> permissions) {
        if (permissions == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(permissions);
    }
}
