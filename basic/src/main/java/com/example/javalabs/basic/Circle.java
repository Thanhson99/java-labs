package com.example.javalabs.basic;

/**
 * Immutable circle implementation based on a radius.
 *
 * @param radius the circle radius, which must be positive
 */
public record Circle(double radius) implements Shape {

    public Circle {
        if (radius <= 0) {
            throw new IllegalArgumentException("radius must be positive");
        }
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }

    @Override
    public String describe() {
        return "Circle(radius=%s)".formatted(radius);
    }
}
