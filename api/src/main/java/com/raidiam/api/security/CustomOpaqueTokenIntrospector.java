package com.raidiam.api.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.SpringOpaqueTokenIntrospector;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Component
public class CustomOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

    private final SpringOpaqueTokenIntrospector delegate;

    public CustomOpaqueTokenIntrospector(
            @Value("${spring.security.oauth2.resourceserver.opaque-token.introspection-uri}") String uri,
            @Value("${spring.security.oauth2.resourceserver.opaque-token.client-id}") String clientId,
            @Value("${spring.security.oauth2.resourceserver.opaque-token.client-secret}") String clientSecret
    ) {

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors()
                .add(new BasicAuthenticationInterceptor(clientId, clientSecret));

        RestOperations restOperations = restTemplate;

        this.delegate = new SpringOpaqueTokenIntrospector(uri, restOperations);
    }

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        try {
            return delegate.introspect(token);
        } catch (OAuth2IntrospectionException ex) {
            if (ex.getCause() instanceof ResourceAccessException) {
                throw new AuthServerUnavailableException("Auth server unavailable", ex);
            }
            throw ex;
        }
    }
}
