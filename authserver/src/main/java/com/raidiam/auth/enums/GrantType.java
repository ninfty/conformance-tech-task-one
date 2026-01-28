package com.raidiam.auth.enums;

import java.util.Arrays;

// https://oauth.net/2/grant-types/
// authorization code = end user
// client credentials = server-to-server
public enum GrantType {
    AUTHORIZATION_CODE("authorization_code"),
    CLIENT_CREDENTIALS("client_credentials");

    private final String value;

    GrantType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static GrantType from(String value) {
        return Arrays.stream(values())
                .filter(g -> g.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported grant_type"));
    }
}
