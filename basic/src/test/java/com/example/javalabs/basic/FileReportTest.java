package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileReportTest {

    @TempDir
    Path tempDir;

    @Test
    void readsOnlyNonBlankLines() throws IOException {
        Path file = tempDir.resolve("notes.txt");
        Files.writeString(file, "Java\n\nSpring\n  \nTesting\n");

        assertEquals(List.of("Java", "Spring", "Testing"), FileReport.readNonBlankLines(file));
    }

    @Test
    void summarizeIncludesFileNameAndLineCount() throws IOException {
        Path file = tempDir.resolve("summary.txt");
        Files.writeString(file, "line-1\nline-2\n");

        String report = FileReport.summarize(file);
        assertTrue(report.contains("summary.txt"));
        assertTrue(report.contains("2 lines"));
    }
}
