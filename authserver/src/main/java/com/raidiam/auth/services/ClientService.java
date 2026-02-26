package com.raidiam.auth.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.raidiam.auth.model.OAuthClient;

@Service
public class ClientService {

    private final Map<String, OAuthClient> clientCache;

    public ClientService(List<OAuthClient> clients) {

        this.clientCache = clients.stream()
                .collect(Collectors.toMap(
                        OAuthClient::getClientId,
                        Function.identity()));
    }

    public OAuthClient authenticate(String clientId, String secret) throws Exception {
        OAuthClient client = clientCache.get(clientId);

        if (client == null) {
            throw new Exception("Client does not exists");
        }

        if (!client.getClientSecret().equals(secret)) {
            throw new Exception("Invalid client credentials");
        }

        return client;
    }
}
