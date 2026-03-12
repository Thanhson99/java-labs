package com.example.demo.system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SystemOverviewControllerTest {

    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String API_KEY_VALUE = "dev-secret-key";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void overviewExposesArchitectureSummary() throws Exception {
        mockMvc.perform(get("/api/system/overview")
                        .header(API_KEY_HEADER, API_KEY_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primaryDatabase.maximumPoolSize").value(5))
                .andExpect(jsonPath("$.analyticsDatabase.jdbcUrl", containsString("analyticsdb")))
                .andExpect(jsonPath("$.architecture").exists());
    }
}
