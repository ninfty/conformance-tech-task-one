package com.raidiam.auth;

import com.raidiam.auth.model.OAuthClient;
import com.raidiam.auth.services.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.raidiam.auth.enums.Scope;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Configuration
public class AuthConfiguration {

    @Bean
    public OAuthClient client() {
        OAuthClient client = new OAuthClient();
        client.setClientId("client1");
        client.setClientSecret("abcde12345");
        client.setTokenLife(3600L);
        client.setScopes(EnumSet.allOf(Scope.class));
        return client;
    }

    @Bean
    public OAuthClient client2() {
        OAuthClient client = new OAuthClient();
        client.setClientId("client2");
        client.setClientSecret("abcde12345");
        client.setTokenLife(3600L);
        client.setScopes(Set.of(Scope.API));
        return client;
    }
}
