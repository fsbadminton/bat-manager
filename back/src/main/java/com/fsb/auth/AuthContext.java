package com.fsb.auth;

public final class AuthContext {
    private static final ThreadLocal<AuthUser> HOLDER = new ThreadLocal<>();

    private AuthContext() {}

    public static void setCurrentUser(AuthUser authUser) {
        HOLDER.set(authUser);
    }

    public static AuthUser getCurrentUser() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}

