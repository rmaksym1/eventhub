package com.eventhub.common.security;

public record AuthenticatedUser(Long userId, String username)
        implements java.security.Principal {

    @Override
    public String getName() {
        return username;
    }
}