package com.wtaumaturgo.benefits.shared.exception;

/**
 * Thrown when a Period cannot be constructed because {@code from} is not
 * strictly before {@code until} (zero-duration or inverted ranges are rejected).
 */
public class InvalidPeriodException extends RuntimeException {

    public InvalidPeriodException(String message) {
        super(message);
    }

    public InvalidPeriodException(String message, Throwable cause) {
        super(message, cause);
    }
}
