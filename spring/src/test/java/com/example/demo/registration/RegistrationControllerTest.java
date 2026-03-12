package com.example.demo.registration;

import com.example.demo.analytics.AnalyticsEventStore;
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

    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String API_KEY_VALUE = "dev-secret-key";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnalyticsEventStore analyticsEventStore;

    @Test
    void registerEndpointCreatesUserAndWritesAnalyticsEvent() throws Exception {
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(API_KEY_HEADER, API_KEY_VALUE)
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
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@example.com"));

        org.assertj.core.api.Assertions.assertThat(analyticsEventStore.countEvents()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void registerEndpointReturnsTooManyRequestsWhenBudgetIsExceeded() throws Exception {
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
                            .header(API_KEY_HEADER, API_KEY_VALUE)
                            .header("X-Caller-Key", "rate-limited-client")
                            .content(payloadTemplate.formatted("rate-" + index, "user" + index)))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(API_KEY_HEADER, API_KEY_VALUE)
                        .header("X-Caller-Key", "rate-limited-client")
                        .content(payloadTemplate.formatted("rate-4", "user4")))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.error", containsString("rate limit exceeded")));
    }

    @Test
    void protectedEndpointsRejectRequestsWithoutApiKey() throws Exception {
        mockMvc.perform(get("/api/users/u-1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("invalid API key"));
    }
}
