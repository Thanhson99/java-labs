package com.example.demo.registration;

import com.example.demo.analytics.AnalyticsEventStore;
import com.example.demo.auth.TokenRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnalyticsEventStore analyticsEventStore;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerEndpointCreatesUserAndWritesAnalyticsEvent() throws Exception {
        String token = fetchToken("student", "student123");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .header("X-Caller-Key", "test-client-create")
                        .content("""
                                {
                                  "userId": "u-1",
                                  "email": "alice@example.com",
                                  "region": "APAC"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accepted").value(true))
                .andExpect(jsonPath("$.userProfile.userId").value("u-1"));

        mockMvc.perform(get("/api/users/u-1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"));

        org.assertj.core.api.Assertions.assertThat(analyticsEventStore.countEvents()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void registerEndpointReturnsTooManyRequestsWhenBudgetIsExceeded() throws Exception {
        String token = fetchToken("student", "student123");
        String payloadTemplate = """
                {
                  "userId": "%s",
                  "email": "%s@example.com",
                  "region": "EU"
                }
                """;

        for (int index = 1; index <= 3; index++) {
            mockMvc.perform(post("/api/users/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
                            .header("X-Caller-Key", "rate-limited-client")
                            .content(payloadTemplate.formatted("rate-" + index, "user" + index)))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .header("X-Caller-Key", "rate-limited-client")
                        .content(payloadTemplate.formatted("rate-4", "user4")))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.error", containsString("rate limit exceeded")));
    }

    @Test
    void protectedEndpointsRejectRequestsWithoutBearerToken() throws Exception {
        mockMvc.perform(get("/api/users/u-1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("authentication required"));
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
