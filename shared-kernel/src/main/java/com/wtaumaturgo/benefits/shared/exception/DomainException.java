package com.wtaumaturgo.benefits.shared.exception;

/**
 * Base for all domain-level business rule violations. The RFC 7807
 * {@code GlobalExceptionHandler} in the {@code app} module catches any
 * subclass and renders it as a {@code ProblemDetail} using
 * {@link #errorCode()} (stable machine-readable identifier) and
 * {@link #errorTitle()} (human-readable pt-BR title for the
 * {@code ProblemDetail.title} field).
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Stable identifier usable as RFC 7807 {@code type} suffix and as a
     * {@code problemDetail.errorCode} custom property. Example:
     * {@code "GRANT_DUPLICATE_ACTIVE"}.
     */
    public abstract String errorCode();

    /**
     * Human-readable pt-BR title displayed in {@code ProblemDetail.title}.
     * Example: {@code "Concessão duplicada"}.
     */
    public abstract String errorTitle();
}
