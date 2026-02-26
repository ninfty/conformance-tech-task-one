package com.raidiam.auth.services;

import com.raidiam.auth.model.AccessToken;
import com.raidiam.auth.model.AccessTokenInfo;
import com.raidiam.auth.model.AccessTokenResponse;
import com.raidiam.auth.model.IntrospectionResponse;
import com.raidiam.auth.model.OAuthClient;
import com.raidiam.auth.model.TokenRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private int port;

    @Autowired
    private OAuthProperties oauthProperties;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ClientService clientService;

    public enum RequestStatus {
        GRANTED,
        UNAUTHORIZED,
        BAD_REQUEST,
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
        // OAuthClient client = clientCache.get(clientId);

        // log.info(clientId);
        // log.info(client.getClientSecret());
        // log.info(tokenRequest.getClientSecret());

        try {
            OAuthClient client = clientService.authenticate(clientId, tokenRequest.getClientSecret());

            
            if (!clientHasScopes(tokenRequest.getScopes(), client)) {
                accessTokenResponse.setRequestStatus(RequestStatus.BAD_REQUEST);
                return accessTokenResponse;
            }

            AccessToken accessToken = tokenService.generateToken(client, tokenRequest.getScopes());

            accessTokenResponse.setRequestStatus(RequestStatus.GRANTED);
            accessTokenResponse.setAccessToken(accessToken);

            return accessTokenResponse;
        } catch (Exception e) {
            accessTokenResponse.setRequestStatus(RequestStatus.UNAUTHORIZED);
            return accessTokenResponse;
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
        return client.getScopes().containsAll(requestScopes);
    }
}
