package com.example.javalabs.basic;

import java.util.ArrayList;
import java.util.List;

/**
 * String-oriented helper methods used to demonstrate common text algorithms.
 */
public final class StringToolkit {

    /**
     * Utility class; instances are not needed because every helper is static.
     */
    private StringToolkit() {
    }

    /**
     * Reverses the order of words while preserving each word's characters.
     *
     * @param sentence a sentence split by whitespace
     * @return a new string with reversed word order
     * @throws IllegalArgumentException when {@code sentence} is blank
     */
    public static String reverseWords(String sentence) {
        requireText(sentence, "sentence");

        String[] parts = sentence.trim().split("\\s+");
        List<String> reversed = new ArrayList<>();
        for (int index = parts.length - 1; index >= 0; index--) {
            reversed.add(parts[index]);
        }
        return String.join(" ", reversed);
    }

    /**
     * Counts vowels in a case-insensitive way.
     *
     * @param text the input text
     * @return the number of vowels in the text
     * @throws IllegalArgumentException when {@code text} is blank
     */
    public static long countVowels(String text) {
        requireText(text, "text");

        long count = 0;
        for (char character : text.toLowerCase().toCharArray()) {
            if ("aeiou".indexOf(character) >= 0) {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks whether a string reads the same forward and backward after basic normalization.
     *
     * @param text the candidate text
     * @return {@code true} when the normalized text is a palindrome
     * @throws IllegalArgumentException when {@code text} is blank
     */
    public static boolean isPalindrome(String text) {
        requireText(text, "text");

        String normalized = text.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
        int left = 0;
        int right = normalized.length() - 1;
        while (left < right) {
            if (normalized.charAt(left) != normalized.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    /**
     * Validates shared text preconditions for the string examples.
     *
     * @param text input text to validate
     * @param name parameter name used in exception messages
     * @throws IllegalArgumentException when {@code text} is {@code null}, empty, or whitespace only
     */
    private static void requireText(String text, String name) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }
}
