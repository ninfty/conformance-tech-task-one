package com.raidiam.auth.services;

import com.raidiam.auth.model.AccessToken;
import com.raidiam.auth.model.AccessTokenInfo;
import com.raidiam.auth.model.AccessTokenResponse;
import com.raidiam.auth.model.IntrospectionResponse;
import com.raidiam.auth.model.OAuthClient;
import com.raidiam.auth.model.TokenRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.MultiValueMap;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthService {

    private int port;
    private final List<String> scopes;

    private static final String SUPPORTED_GRANT = "client_credentials";

    public enum RequestStatus {
        GRANTED,
        UNAUTHORIZED,
        BAD_REQUEST,
    }

    private Map<String, OAuthClient> clientCache = new HashMap<>();
    private Map<String, AccessTokenInfo> accessTokenCache = new HashMap<>();

    public AuthService(List<OAuthClient> clients, List<String> scopes) {
       clients.stream().forEach(client -> {
           clientCache.put(client.getClientId(), client);
       });
       this.scopes = scopes;
    }

    public Map<String, Object> discovery() {
        return Map.of(
                "issuer", "http://localhost:8081",
                "token_endpoint", "http://localhost:8081/token",
                "introspection_endpoint", "http://localhost:8081/token/introspect",
                "grant_types_supported", List.of("client_credentials"),
                "client_authentication_methods_supported", List.of("client_secret"),
                "scopes_supported", scopes
        );
    }

    public AccessTokenResponse requestToken(MultiValueMap<String, String> params) {
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
        TokenRequest tokenRequest = null;
        try {
             tokenRequest = toTokenRequest(params);
        } catch (IllegalArgumentException ex) {
            accessTokenResponse.setRequestStatus(RequestStatus.BAD_REQUEST);
            return accessTokenResponse;
        }

        if (!SUPPORTED_GRANT.contains(tokenRequest.getGrantType())) {
            accessTokenResponse.setRequestStatus(RequestStatus.BAD_REQUEST);
            return accessTokenResponse;
        }

        String clientId = tokenRequest.getClientId();
        OAuthClient client = clientCache.get(clientId);
        if (client == null) {
            accessTokenResponse.setRequestStatus(RequestStatus.UNAUTHORIZED);
            return accessTokenResponse;
        }

        if(!client.getClientSecret().equals(tokenRequest.getClientSecret())){
            accessTokenResponse.setRequestStatus(RequestStatus.UNAUTHORIZED);
            return accessTokenResponse;
        }

        for (String requestScope : tokenRequest.getScopes()) {
            if (!client.getScopes().contains(requestScope)) {
                accessTokenResponse.setRequestStatus(RequestStatus.BAD_REQUEST);
                return accessTokenResponse;
            }
        }

        AccessToken accessToken = new AccessToken();
        String tokenValue = RandomStringUtils.randomAlphanumeric(64);
        accessToken.setAccessToken(tokenValue);
        accessToken.setExpiresIn(client.getTokenLife());
        accessToken.setScope(String.join(" ", tokenRequest.getScopes()));
        AccessTokenInfo accessTokenInfo = new AccessTokenInfo();
        accessTokenInfo.setAccessToken(accessToken);
        accessTokenInfo.setClient(client);
        accessTokenCache.put(tokenValue, accessTokenInfo);
        accessTokenResponse.setRequestStatus(RequestStatus.GRANTED);
        accessTokenResponse.setAccessToken(accessToken);
        return  accessTokenResponse;
    }


    public IntrospectionResponse introspect(MultiValueMap<String, String> params) {
        IntrospectionResponse response = new IntrospectionResponse();

        try {
            String token = extract("token", params);
            AccessTokenInfo accessTokenInfo = accessTokenCache.get(token);
            if (accessTokenInfo == null) {
                response.setActive(false);
                return response;
            }
            Instant now = Instant.now();
            Instant iat = accessTokenInfo.getIat();
            AccessToken issuedToken = accessTokenInfo.getAccessToken();
            Instant exp = iat.plusSeconds(issuedToken.getExpiresIn());
            if(now.isAfter(exp)){
                response.setActive(false);
                return response;
            }
            response.setActive(true);
            response.setClientId(accessTokenInfo.getClient().getClientId());
            response.setScope(accessTokenInfo.getAccessToken().getScope());
            return  response;

        } catch (IllegalArgumentException e) {
            response.setActive(false);
            return  response;
        }

    }

    private TokenRequest toTokenRequest(MultiValueMap<String, String> params) {
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setClientId(extract("client_id", params));
        tokenRequest.setClientSecret(extract("client_secret", params));
        tokenRequest.setGrantType(extract("grant_type", params));

        if (params.containsKey("scope")) {
            List<String> scopesRequested = Arrays.asList(params.getFirst("scope").split(" "));

            scopesRequested.forEach(scope -> {
                tokenRequest.addScope(scope);
            });
        }
        
        return tokenRequest;
    }

    private String extract(String field, MultiValueMap<String, String> params) {
        if(!params.containsKey(field)){
            throw new IllegalArgumentException("Missing " + field);
        }
        return params.getFirst(field);
    }

}
