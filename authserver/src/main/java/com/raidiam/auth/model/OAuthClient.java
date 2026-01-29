package com.raidiam.auth.model;

import java.util.Set;

import com.raidiam.auth.enums.Scope;

public class OAuthClient {

    private String clientId;
    private String clientSecret;
    private Set<Scope> scopes;
    private Long tokenLife;

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setScopes(Set<Scope> scopes) {
        this.scopes = scopes;
    }

    public Set<Scope> getScopes() {
        return scopes;
    }

    public Long getTokenLife() {
        return tokenLife;
    }

    public void setTokenLife(Long tokenLife) {
        this.tokenLife = tokenLife;
    }
}
