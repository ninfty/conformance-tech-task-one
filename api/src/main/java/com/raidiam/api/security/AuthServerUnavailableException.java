package com.raidiam.api.security;

import org.springframework.security.core.AuthenticationException;

public class AuthServerUnavailableException extends AuthenticationException {

    public AuthServerUnavailableException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AuthServerUnavailableException(String msg) {
        super(msg);
    }
}
