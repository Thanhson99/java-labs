package com.example.javalabs.basic;

/**
 * Immutable rectangle implementation based on width and height.
 *
 * @param width the width, which must be positive
 * @param height the height, which must be positive
 */
public record Rectangle(double width, double height) implements Shape {

    /**
     * Validates the generated record constructor.
     *
     * @throws IllegalArgumentException when width or height is zero or negative
     */
    public Rectangle {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("width and height must be positive");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double area() {
        return width * height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String describe() {
        return "Rectangle(width=%s, height=%s)".formatted(width, height);
    }
}
