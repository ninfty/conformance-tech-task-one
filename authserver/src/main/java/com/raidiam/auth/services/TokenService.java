package com.raidiam.auth.services;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.raidiam.auth.enums.Scope;
import com.raidiam.auth.model.AccessToken;
import com.raidiam.auth.model.AccessTokenInfo;
import com.raidiam.auth.model.IntrospectionResponse;
import com.raidiam.auth.model.OAuthClient;
import com.raidiam.auth.services.AuthService.RequestStatus;

@Service
public class TokenService {

    @Autowired
    private TokenStore tokenStore;

    public AccessToken generateToken(OAuthClient client, Set<Scope> scopes) {
        String tokenValue = RandomStringUtils.secure().nextAlphanumeric(64);

        AccessToken accessToken = new AccessToken();
        accessToken.setAccessToken(tokenValue);
        accessToken.setExpiresIn(client.getTokenLife());
        accessToken.setScope(
                scopes.stream()
                        .map(Scope::getValue)
                        .collect(Collectors.joining(" ")));

        AccessTokenInfo accessTokenInfo = new AccessTokenInfo();
        accessTokenInfo.setAccessToken(accessToken);
        accessTokenInfo.setClient(client);

        tokenStore.save(tokenValue, accessTokenInfo);

        return accessToken;
    }

    public IntrospectionResponse introspect(String token) throws Exception {

        AccessTokenInfo accessTokenInfo = tokenStore.find(token)
                .orElseThrow(() -> new Exception("Invalid token"));

        Instant iat = accessTokenInfo.getIat();
        AccessToken issuedToken = accessTokenInfo.getAccessToken();
        Instant exp = iat.plusSeconds(issuedToken.getExpiresIn());

        IntrospectionResponse response = new IntrospectionResponse();

        if (Instant.now().isAfter(exp)) {
            throw new Exception("Invalid token");
        }

        response.setActive(true);
        response.setClientId(accessTokenInfo.getClient().getClientId());
        response.setScope(accessTokenInfo.getAccessToken().getScope());

        return response;
    }
}
