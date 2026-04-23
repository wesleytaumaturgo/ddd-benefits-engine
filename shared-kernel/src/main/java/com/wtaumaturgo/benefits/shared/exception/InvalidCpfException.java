package com.wtaumaturgo.benefits.shared.exception;

/**
 * Thrown when a CPF string fails validation at construction time.
 * Reasons include: wrong length, repeating sequence, or invalid check digits.
 */
public class InvalidCpfException extends RuntimeException {

    public InvalidCpfException(String message) {
        super(message);
    }

    public InvalidCpfException(String message, Throwable cause) {
        super(message, cause);
    }
}
