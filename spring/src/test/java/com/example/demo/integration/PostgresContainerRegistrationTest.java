package com.example.demo.integration;

import com.example.demo.auth.TokenRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class PostgresContainerRegistrationTest {

    @Container
    static PostgreSQLContainer<?> primaryDatabase = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("usersdb")
            .withUsername("postgres")
            .withPassword("postgres");

    @Container
    static PostgreSQLContainer<?> analyticsDatabase = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("analyticsdb")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", primaryDatabase::getJdbcUrl);
        registry.add("spring.datasource.username", primaryDatabase::getUsername);
        registry.add("spring.datasource.password", primaryDatabase::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("app.analytics.datasource.url", analyticsDatabase::getJdbcUrl);
        registry.add("app.analytics.datasource.username", analyticsDatabase::getUsername);
        registry.add("app.analytics.datasource.password", analyticsDatabase::getPassword);
        registry.add("app.analytics.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.h2.console.enabled", () -> false);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerFlowWorksAgainstRealPostgresContainers() throws Exception {
        String token = fetchToken("admin", "admin123");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .header("X-Caller-Key", "postgres-test-client")
                        .content("""
                                {
                                  \"userId\": \"pg-1\",
                                  \"email\": \"pg1@example.com\",
                                  \"region\": \"EU\"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accepted").value(true));

        mockMvc.perform(get("/api/system/overview")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primaryDatabase.jdbcUrl", containsString("jdbc:postgresql")));
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
