package com.example.demo.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RefreshTokenStore refreshTokenStore;

    @Test
    void tokenEndpointReturnsJwtForValidCredentials() throws Exception {
        mockMvc.perform(post("/api/auth/token")
                        .header("X-Session-Label", "student-browser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  \"username\": \"student\",
                                  \"password\": \"student123\"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken", not(isEmptyOrNullString())))
                .andExpect(jsonPath("$.refreshToken", not(isEmptyOrNullString())))
                .andExpect(jsonPath("$.sessionId", not(isEmptyOrNullString())));
    }

    @Test
    void refreshEndpointRotatesRefreshTokenAndReturnsNewAccessToken() throws Exception {
        String token = fetchRefreshToken();

        mockMvc.perform(post("/api/auth/refresh")
                        .header("X-Session-Label", "student-refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "%s"
                                }
                                """.formatted(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", not(isEmptyOrNullString())))
                .andExpect(jsonPath("$.refreshToken", not(isEmptyOrNullString())))
                .andExpect(jsonPath("$.sessionId", not(isEmptyOrNullString())));
    }

    @Test
    void refreshEndpointRejectsReusedRefreshToken() throws Exception {
        String token = fetchRefreshToken();

        mockMvc.perform(post("/api/auth/refresh")
                        .header("X-Session-Label", "student-refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "%s"
                                }
                                """.formatted(token)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "%s"
                                }
                                """.formatted(token)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("refresh token reuse detected"));
    }

    @Test
    void logoutEndpointRevokesRefreshToken() throws Exception {
        int before = refreshTokenStore.activeTokenCountForUser("student");
        String token = fetchRefreshToken();

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "%s"
                                }
                                """.formatted(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("refresh token revoked"));

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "%s"
                                }
                                """.formatted(token)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("refresh token reuse detected"));

        org.assertj.core.api.Assertions.assertThat(refreshTokenStore.activeTokenCountForUser("student"))
                .isEqualTo(before);
    }

    @Test
    void logoutAllRevokesEveryActiveTokenForUser() throws Exception {
        int existingTokens = refreshTokenStore.activeTokenCountForUser("student");
        fetchRefreshToken();
        fetchRefreshToken();

        mockMvc.perform(post("/api/auth/logout-all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "student",
                                  "password": "student123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("all refresh tokens revoked"))
                .andExpect(jsonPath("$.revokedCount").value(Integer.toString(existingTokens + 2)));

        org.assertj.core.api.Assertions.assertThat(refreshTokenStore.activeTokenCountForUser("student"))
                .isZero();
    }

    private String fetchRefreshToken() throws Exception {
        String refreshToken = mockMvc.perform(post("/api/auth/token")
                        .header("X-Session-Label", "student-browser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "student",
                                  "password": "student123"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(refreshToken)
                .get("refreshToken")
                .asText();
    }
}
