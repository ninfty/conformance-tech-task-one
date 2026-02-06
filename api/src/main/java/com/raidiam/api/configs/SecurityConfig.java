package com.raidiam.api.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import com.raidiam.api.security.CustomAuthEntryPoint;
import com.raidiam.api.security.CustomOpaqueTokenIntrospector;
import com.raidiam.api.security.CustomAccessDeniedHandler;

@Configuration
public class SecurityConfig {

    private final CustomAuthEntryPoint customAuthEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomOpaqueTokenIntrospector customOpaqueTokenIntrospector;

    public SecurityConfig(CustomAuthEntryPoint customAuthEntryPoint,
            CustomAccessDeniedHandler customAccessDeniedHandler,
            CustomOpaqueTokenIntrospector customOpaqueTokenIntrospector) {
        this.customAuthEntryPoint = customAuthEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.customOpaqueTokenIntrospector = customOpaqueTokenIntrospector;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/health").permitAll()
                        .requestMatchers("/api/now").hasAuthority("SCOPE_time")
                        .requestMatchers("/api/random").hasAuthority("SCOPE_random")
                        .anyRequest().hasAuthority("SCOPE_api"))
                .oauth2ResourceServer(
                        oauth2 -> oauth2.opaqueToken(token -> token.introspector(customOpaqueTokenIntrospector))
                                .authenticationEntryPoint(customAuthEntryPoint))
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedHandler(customAccessDeniedHandler));

        return http.build();
    }
}
