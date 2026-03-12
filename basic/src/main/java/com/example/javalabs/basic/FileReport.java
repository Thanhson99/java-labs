package com.example.javalabs.basic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Demonstrates modern file I/O with {@link Path} and {@link Files}.
 */
public final class FileReport {

    private FileReport() {
    }

    /**
     * Reads all non-blank lines from a file.
     *
     * @param path the file path to read
     * @return non-blank lines in original order
     * @throws IOException when the file cannot be read
     */
    public static List<String> readNonBlankLines(Path path) throws IOException {
        return Files.readAllLines(path).stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();
    }

    /**
     * Produces a compact text report about a file.
     *
     * @param path the file path to inspect
     * @return a summary containing the line count and file size
     * @throws IOException when the file cannot be read
     */
    public static String summarize(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        long size = Files.size(path);
        return "File '%s' has %d lines and %d bytes."
                .formatted(path.getFileName(), lines.size(), size);
    }
}
