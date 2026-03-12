package com.example.demo.system;

import com.example.demo.auth.TokenRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SystemOverviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void overviewExposesArchitectureSummaryForAdmin() throws Exception {
        String token = fetchToken("admin", "admin123");

        mockMvc.perform(get("/api/system/overview")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primaryDatabase.maximumPoolSize").value(5))
                .andExpect(jsonPath("$.analyticsDatabase.jdbcUrl", containsString("analyticsdb")))
                .andExpect(jsonPath("$.messaging.kafkaEnabled").value(false))
                .andExpect(jsonPath("$.messaging.rabbitmqEnabled").value(false))
                .andExpect(jsonPath("$.messaging.consumedCounts").exists())
                .andExpect(jsonPath("$.architecture").exists());
    }

    @Test
    void overviewRejectsRegularUserRole() throws Exception {
        String token = fetchToken("student", "student123");

        mockMvc.perform(get("/api/system/overview")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("forbidden"));
    }

    private String fetchToken(String username, String password) throws Exception {
        String body = objectMapper.writeValueAsString(new TokenRequest(username, password));
        String response = mockMvc.perform(post("/api/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", not(isEmptyOrNullString())))
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("accessToken").asText();
    }
}
