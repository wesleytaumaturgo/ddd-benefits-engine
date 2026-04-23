package com.wtaumaturgo.benefits.shared.exception;

/**
 * Thrown when a Period cannot be constructed because {@code from} is not
 * strictly before {@code until} (zero-duration or inverted ranges are rejected).
 */
public class InvalidPeriodException extends DomainException {

    public InvalidPeriodException(String message) {
        super(message);
    }

    public InvalidPeriodException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override public String errorCode() { return "SHARED_INVALID_PERIOD"; }

    @Override public String errorTitle() { return "Período inválido"; }
}
