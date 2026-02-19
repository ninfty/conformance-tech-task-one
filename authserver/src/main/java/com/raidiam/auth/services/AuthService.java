package com.raidiam.auth.services;

import com.raidiam.auth.model.AccessToken;
import com.raidiam.auth.model.AccessTokenInfo;
import com.raidiam.auth.model.AccessTokenResponse;
import com.raidiam.auth.model.IntrospectionResponse;
import com.raidiam.auth.model.OAuthClient;
import com.raidiam.auth.model.TokenRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;

import com.raidiam.auth.config.OAuthProperties;
import com.raidiam.auth.enums.GrantType;
import com.raidiam.auth.enums.Scope;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private int port;

    @Autowired
    private OAuthProperties oauthProperties;

    public enum RequestStatus {
        GRANTED,
        UNAUTHORIZED,
        BAD_REQUEST,
    }

    private Map<String, OAuthClient> clientCache = new HashMap<>();
    private Map<String, AccessTokenInfo> accessTokenCache = new HashMap<>();

    public AuthService(List<OAuthClient> clients) {
        clients.stream().forEach(client -> {
            clientCache.put(client.getClientId(), client);
        });
    }

    public Map<String, Object> discovery() {
        return Map.of(
                "issuer", "http://localhost:8081",
                "token_endpoint", "http://localhost:8081/token",
                "introspection_endpoint", "http://localhost:8081/token/introspect",
                "grant_types_supported", List.of("client_credentials"),
                "client_authentication_methods_supported", List.of("client_secret"),
                "scopes_supported", Arrays.stream(Scope.values()).map(Scope::getValue).collect(Collectors.toSet()));
    }

    public AccessTokenResponse requestToken(MultiValueMap<String, String> params) {
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
        TokenRequest tokenRequest = null;
        GrantType grantType;

        try {
            tokenRequest = toTokenRequest(params);
        } catch (IllegalArgumentException ex) {
            accessTokenResponse.setRequestStatus(RequestStatus.BAD_REQUEST);
            return accessTokenResponse;
        }

        try {
            grantType = GrantType.from(tokenRequest.getGrantType());

            if (!oauthProperties.isEnabled(grantType)) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException ex) {
            accessTokenResponse.setRequestStatus(RequestStatus.BAD_REQUEST);
            return accessTokenResponse;
        }

        String clientId = tokenRequest.getClientId();
        OAuthClient client = clientCache.get(clientId);

        // log.info(clientId);
        // log.info(client.getClientSecret());
        // log.info(tokenRequest.getClientSecret());

        if (client == null) {
            accessTokenResponse.setRequestStatus(RequestStatus.UNAUTHORIZED);
            return accessTokenResponse;
        }

        if (!clicentSecretIsCorrect(client, tokenRequest)) {
            accessTokenResponse.setRequestStatus(RequestStatus.UNAUTHORIZED);
            return accessTokenResponse;
        }

        if (!clientHasScopes(tokenRequest.getScopes(), client)) {
            accessTokenResponse.setRequestStatus(RequestStatus.BAD_REQUEST);
            return accessTokenResponse;
        }

        AccessToken accessToken = new AccessToken();
        String tokenValue = RandomStringUtils.secure().nextAlphanumeric(64);
        accessToken.setAccessToken(tokenValue);
        accessToken.setExpiresIn(client.getTokenLife());

        accessToken.setScope(
                tokenRequest.getScopes().stream()
                        .map(Scope::getValue)
                        .collect(Collectors.joining(" ")));

        AccessTokenInfo accessTokenInfo = new AccessTokenInfo();
        accessTokenInfo.setAccessToken(accessToken);
        accessTokenInfo.setClient(client);
        accessTokenCache.put(tokenValue, accessTokenInfo);
        accessTokenResponse.setRequestStatus(RequestStatus.GRANTED);
        accessTokenResponse.setAccessToken(accessToken);

        return accessTokenResponse;
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

            if (now.isAfter(exp)) {
                response.setActive(false);
                return response;
            }

            response.setActive(true);
            response.setClientId(accessTokenInfo.getClient().getClientId());
            response.setScope(accessTokenInfo.getAccessToken().getScope());

            return response;

        } catch (IllegalArgumentException e) {
            response.setActive(false);
            return response;
        }

    }

    private TokenRequest toTokenRequest(MultiValueMap<String, String> params) {
        TokenRequest tokenRequest = new TokenRequest();

        tokenRequest.setClientId(extract("client_id", params));
        tokenRequest.setClientSecret(extract("client_secret", params));
        tokenRequest.setGrantType(extract("grant_type", params));

        if (params.containsKey("scope")) {
            String rawScope = params.getFirst("scope");

            for (String scopeValue : rawScope.split(" ")) {
                tokenRequest.addScope(Scope.from(scopeValue));
            }
        }

        return tokenRequest;
    }

    private String extract(String field, MultiValueMap<String, String> params) {
        if (!params.containsKey(field)) {
            throw new IllegalArgumentException("Missing " + field);
        }

        return params.getFirst(field);
    }

    private Boolean clientHasScopes(Set<Scope> requestScopes, OAuthClient client) {
        for (Scope requestedScope : requestScopes) {
            if (!client.getScopes().contains(requestedScope)) {
                return false;
            }
        }

        return true;
    }

    private Boolean clicentSecretIsCorrect(OAuthClient client, TokenRequest tokenRequest) {
        return client.getClientSecret().equals(tokenRequest.getClientSecret());
    }
}
