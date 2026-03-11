package com.example.springsecurityapplication.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class SecurityAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void anonymous_canAccessAuthenticationPage() throws Exception {
        mockMvc.perform(get("/authentication"))
                .andExpect(status().isOk());
    }

    @Test
    void anonymous_cannotAccessAdmin_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/authentication"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void user_cannotAccessAdmin_forbidden() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void admin_canAccessAdmin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk());
    }
}