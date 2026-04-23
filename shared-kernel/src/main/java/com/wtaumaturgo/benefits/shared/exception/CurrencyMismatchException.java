package com.wtaumaturgo.benefits.shared.exception;

import java.util.Currency;

/**
 * Thrown when an arithmetic or comparison operation is attempted between
 * Money values denominated in different currencies.
 */
public class CurrencyMismatchException extends RuntimeException {

    public CurrencyMismatchException(Currency expected, Currency actual) {
        super("Currency mismatch: expected " + expected + " but got " + actual);
    }
}
