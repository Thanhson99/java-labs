package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void homePageIsPublicAndShowsAdminLink() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello,")))
                .andExpect(content().string(containsString("Switch To Admin")))
                .andExpect(content().string(containsString("Cheat Sheets")))
                .andExpect(content().string(containsString("Roadmap")))
                .andExpect(content().string(containsString("Admin Console")));
    }

    @Test
    void adminPageIsPublicAndShowsAdminControls() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Admin Console")))
                .andExpect(content().string(containsString("Load Admin Data")))
                .andExpect(content().string(containsString("admin / admin123")));
    }

    @Test
    void cheatSheetPageIsPublic() throws Exception {
        mockMvc.perform(get("/cheatsheets/java-basics"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Java Basics Cheat Sheet")))
                .andExpect(content().string(containsString("Variables,")))
                .andExpect(content().string(containsString("Method Anatomy")))
                .andExpect(content().string(containsString("Collections And Streams")))
                .andExpect(content().string(containsString("Spring MVC And REST")))
                .andExpect(content().string(containsString("Testing Patterns")));
    }

    @Test
    void roadmapPageIsPublic() throws Exception {
        mockMvc.perform(get("/roadmap"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Learning Roadmap")))
                .andExpect(content().string(containsString("Phase 1: Java Core")))
                .andExpect(content().string(containsString("Phase 4: What To Build Next")))
                .andExpect(content().string(containsString("Definition Of Done")))
                .andExpect(content().string(containsString("Reset Roadmap Progress")));
    }
}
