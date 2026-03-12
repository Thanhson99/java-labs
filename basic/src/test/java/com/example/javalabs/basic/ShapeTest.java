package com.example.javalabs.basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShapeTest {

    @Test
    void circleAreaUsesPiR2() {
        Circle circle = new Circle(2.0);
        assertEquals(Math.PI * 4.0, circle.area(), 0.0001);
    }

    @Test
    void rectangleAreaUsesWidthTimesHeight() {
        Rectangle rectangle = new Rectangle(3.0, 4.0);
        assertEquals(12.0, rectangle.area(), 0.0001);
    }

    @Test
    void invalidDimensionsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Circle(0));
        assertThrows(IllegalArgumentException.class, () -> new Rectangle(3, -1));
    }
}
