package com.raidiam.auth.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.raidiam.auth.model.AccessTokenInfo;

@Service
public class TokenStore {
    
    private Map<String, AccessTokenInfo> accessTokenCache = new HashMap<>();

    public void save(String token, AccessTokenInfo info) {
        accessTokenCache.put(token, info);
    }

    public Optional<AccessTokenInfo> find(String token) {
        return Optional.ofNullable(accessTokenCache.get(token));
    }
}
