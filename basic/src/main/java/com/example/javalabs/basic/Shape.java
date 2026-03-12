package com.example.javalabs.basic;

/**
 * A sealed interface restricts which classes are allowed to implement it.
 *
 * <p>This is useful when the set of supported subtypes is intentionally finite.</p>
 */
public sealed interface Shape permits Circle, Rectangle {

    /**
     * Calculates the surface area of the shape.
     *
     * @return the area in square units
     */
    double area();

    /**
     * Returns a short text description of the shape instance.
     *
     * @return a human-readable description
     */
    String describe();
}
