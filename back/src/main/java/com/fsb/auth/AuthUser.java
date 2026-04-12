package com.fsb.auth;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AuthUser {
    private Long userId;
    private String username;
    private String role;
    private List<String> permissions;

    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }
}

