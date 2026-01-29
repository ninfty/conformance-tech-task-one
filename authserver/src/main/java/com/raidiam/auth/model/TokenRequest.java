package com.raidiam.auth.model;

import java.util.EnumSet;
import java.util.Set;

import com.raidiam.auth.enums.Scope;

public class TokenRequest {

    private String clientId;
    private String clientSecret;
    private String grantType;
    private Set<Scope> scopes = EnumSet.noneOf(Scope.class);

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public Set<Scope> getScopes() {
        return scopes;
    }

    public void setScopes(Set<Scope> scopes) {
        this.scopes = scopes;
    }

    public void addScope(Scope scope) {
        scopes.add(scope);
    }

}
