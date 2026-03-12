package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AsyncExamplesTest {

    @Test
    void buildUserReportCombinesAsyncResults() {
        String report = AsyncExamples.buildUserReport("Ada").join();
        assertTrue(report.contains("Hello, Ada!"));
        assertTrue(report.contains("30"));
    }
}
