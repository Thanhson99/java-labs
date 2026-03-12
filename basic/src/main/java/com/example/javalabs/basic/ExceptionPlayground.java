package com.example.javalabs.basic;

/**
 * Centralizes a few examples of validation and exception handling.
 */
public final class ExceptionPlayground {

    private ExceptionPlayground() {
    }

    /**
     * Divides two numbers and wraps invalid input in a user-friendly exception.
     *
     * @param dividend the value being divided
     * @param divisor the value that divides the dividend
     * @return the division result
     */
    public static double safeDivide(double dividend, double divisor) {
        if (divisor == 0) {
            throw new IllegalArgumentException("divisor must not be zero");
        }
        return dividend / divisor;
    }

    /**
     * Parses an integer while converting low-level parsing errors into a domain message.
     *
     * @param text the text to parse
     * @return the parsed integer
     */
    public static int parsePositiveInt(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("text must not be blank");
        }

        try {
            int value = Integer.parseInt(text);
            if (value <= 0) {
                throw new IllegalArgumentException("value must be positive");
            }
            return value;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("text must contain a valid integer", exception);
        }
    }
}
