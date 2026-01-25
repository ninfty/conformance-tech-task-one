package com.raidiam.auth.enums;

// https://oauth.net/2/grant-types/
// authorization code = user
// client credentials = server-to-server
public enum GrantType {
    AUTHORIZATION_CODE("authorization_code"),
    CLIENT_CREDENTIALS("client_credentials");

    public final String grantType;

    private GrantType(String grantType) {
        this.grantType = grantType;
    }

    @Override
    public String toString() {
        return this.grantType;
    }
}
