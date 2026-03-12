package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringToolkitTest {

    @Test
    void reverseWordsReordersByWhitespaceSeparatedTokens() {
        assertEquals("fun is java", StringToolkit.reverseWords("java is fun"));
    }

    @Test
    void countVowelsIsCaseInsensitive() {
        assertEquals(6L, StringToolkit.countVowels("Documentation"));
    }

    @Test
    void palindromeIgnoresCaseAndPunctuation() {
        assertTrue(StringToolkit.isPalindrome("Never odd or even"));
        assertFalse(StringToolkit.isPalindrome("Java"));
    }

    @Test
    void blankInputIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> StringToolkit.reverseWords(" "));
    }
}
