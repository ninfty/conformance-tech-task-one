package com.raidiam.api.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAllowAccessToTimeEndpointWithTimeScope() throws Exception {
        mockMvc.perform(
                get("/api/now")
                    .with(user("client1")
                        .authorities(() -> "SCOPE_time"))
            )
            .andExpect(status().isOk());
    }

    @Test
    void shouldDenyAccessToTimeEndpointWithWrongScope() throws Exception {
        mockMvc.perform(
                get("/api/now")
                    .with(user("client1")
                        .authorities(() -> "SCOPE_wrong"))
            )
            .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenNoAuthenticationIsProvided() throws Exception {
        mockMvc.perform(get("/api/now"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowAccessWhenTokenHasMultipleScopes() throws Exception {
        mockMvc.perform(
            get("/api/now")
                .with(user("client1").authorities(
                    () -> "SCOPE_api",
                    () -> "SCOPE_time"
                ))
        ).andExpect(status().isOk());
    }

    @Test
    void shouldAllowAccessToRandomEndpointWithRandomScope() throws Exception {
        mockMvc.perform(
                get("/api/random")
                    .with(user("client1")
                        .authorities(() -> "SCOPE_random"))
            )
            .andExpect(status().isOk());
    }

    @Test
    void shouldDenyAccessToRandomEndpointWithWrongScope() throws Exception {
        mockMvc.perform(
                get("/api/random")
                    .with(user("client1")
                        .authorities(() -> "SCOPE_wrong"))
            )
            .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAccessToHealthEndpointWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/health"))
            .andExpect(status().isOk());
    }
}