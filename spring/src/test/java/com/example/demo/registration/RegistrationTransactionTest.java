package com.example.demo.registration;

import com.example.demo.audit.RegistrationAuditStore;
import com.example.demo.auth.TokenRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.profile.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationTransactionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private RegistrationAuditStore registrationAuditStore;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void rollbackDemoRemovesPrimaryWritesWhenFailureIsTriggered() throws Exception {
        String token = fetchToken("admin", "admin123");

        mockMvc.perform(post("/api/users/register-demo")
                        .param("failAfterAudit", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .header("X-Caller-Key", "rollback-client")
                        .content("""
                                {
                                  "userId": "rollback-1",
                                  "email": "rollback@example.com",
                                  "region": "US"
                                }
                                """))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("simulated failure after audit write"));

        assertThat(userProfileRepository.findById("rollback-1")).isEmpty();
        assertThat(registrationAuditStore.countByUserId("rollback-1")).isZero();
    }

    private String fetchToken(String username, String password) throws Exception {
        String body = objectMapper.writeValueAsString(new TokenRequest(username, password));
        String response = mockMvc.perform(post("/api/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("accessToken").asText();
    }
}
