package com.raidiam.auth;

import com.raidiam.auth.model.OAuthClient;
import com.raidiam.auth.services.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AuthConfiguration {

    private List<String> scopes = List.of("api", "time", "random");
    
    @Bean
    public OAuthClient client() {
        OAuthClient client = new OAuthClient();
        client.setClientId("client1");
        client.setClientSecret("abcde12345");
        client.setTokenLife(3600L);
        client.setScopes(scopes);
        return client;
    }

    @Bean
    public OAuthClient clientRandom() {
        OAuthClient client = new OAuthClient();
        client.setClientId("client-random");
        client.setClientSecret("abcde12345");
        client.setTokenLife(3600L);
        client.setScopes(List.of("random"));
        return client;
    }

    @Bean
    public OAuthClient clientTime() {
        OAuthClient client = new OAuthClient();
        client.setClientId("client-time");
        client.setClientSecret("abcde12345");
        client.setTokenLife(3600L);
        client.setScopes(List.of("time"));
        return client;
    }

    @Bean
    public OAuthClient clientApi() {
        OAuthClient client = new OAuthClient();
        client.setClientId("client-api");
        client.setClientSecret("abcde12345");
        client.setTokenLife(3600L);
        client.setScopes(List.of("api"));
        return client;
    }

    @Bean
    public AuthService authService(List<OAuthClient> clients) {
        AuthService authService = new AuthService(clients, scopes);
        return authService;
    }

}
