package com.example.javalabs.basic;

/**
 * Centralizes a few examples of validation and exception handling.
 *
 * <p>The methods are intentionally small so learners can clearly separate three ideas:
 * validate input early, throw meaningful domain-level messages, and preserve the original cause
 * when wrapping lower-level exceptions.</p>
 */
public final class ExceptionPlayground {

    /**
     * Utility class; instances are not needed because all examples are stateless.
     */
    private ExceptionPlayground() {
    }

    /**
     * Divides two numbers and wraps invalid input in a user-friendly exception.
     *
     * @param dividend the value being divided
     * @param divisor the value that divides the dividend
     * @return the division result
     * @throws IllegalArgumentException when {@code divisor} is zero
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
     * @throws IllegalArgumentException when the text is blank, not numeric, or not positive
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
            // Keep the original parsing failure as the cause so debugging still has full context.
            throw new IllegalArgumentException("text must contain a valid integer", exception);
        }
    }
}
