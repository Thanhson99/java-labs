package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PairTest {

    @Test
    void genericPairKeepsBothTypes() {
        Pair<String, Integer> pair = Pair.of("Java", 17);
        assertEquals("Java", pair.left());
        assertEquals(17, pair.right());
    }
}
