package com.raidiam.auth.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.raidiam.auth.enums.GrantType;

@ConfigurationProperties(prefix = "oauth")
@Component
public class OAuthProperties {
    private Set<String> enabledGrantTypes = new HashSet<>();

    public Set<String> getEnabledGrantTypes() {
        return enabledGrantTypes;
    }

    public void setEnabledGrantTypes(Set<String> enabledGrantTypes) {
        this.enabledGrantTypes = enabledGrantTypes;
    }

    public boolean isEnabled(GrantType grantType) {
        return enabledGrantTypes.contains(grantType.getValue());
    }
}
