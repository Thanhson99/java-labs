package com.example.demo.registration;

import com.example.demo.audit.RegistrationAuditStore;
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

    @Test
    void rollbackDemoRemovesPrimaryWritesWhenFailureIsTriggered() throws Exception {
        mockMvc.perform(post("/api/users/register-demo")
                        .param("failAfterAudit", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-Key", "dev-secret-key")
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
}
