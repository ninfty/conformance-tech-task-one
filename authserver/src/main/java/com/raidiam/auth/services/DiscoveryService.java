package com.raidiam.auth.services;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.raidiam.auth.enums.Scope;

@Service
public class DiscoveryService {

    public Map<String, Object> discovery() {
        return Map.of(
                "issuer", "http://localhost:8081",
                "token_endpoint", "http://localhost:8081/token",
                "introspection_endpoint", "http://localhost:8081/token/introspect",
                "grant_types_supported", List.of("client_credentials"),
                "client_authentication_methods_supported", List.of("client_secret"),
                "scopes_supported", Arrays.stream(Scope.values()).map(Scope::getValue).collect(Collectors.toSet()));
    }
}
