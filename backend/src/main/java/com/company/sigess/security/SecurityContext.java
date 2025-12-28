package com.company.sigess.security;

public class SecurityContext {
    private static final ThreadLocal<UserPrincipal> userThreadLocal = new ThreadLocal<>();

    public static void setUser(UserPrincipal user) {
        userThreadLocal.set(user);
    }

    public static UserPrincipal getUser() {
        return userThreadLocal.get();
    }

    public static void clear() {
        userThreadLocal.remove();
    }
}
