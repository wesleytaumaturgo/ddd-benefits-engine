package com.wtaumaturgo.benefits.shared.exception;

import java.util.Currency;

/**
 * Thrown when an arithmetic or comparison operation is attempted between
 * Money values denominated in different currencies.
 */
public class CurrencyMismatchException extends DomainException {

    public CurrencyMismatchException(Currency expected, Currency actual) {
        super("Currency mismatch: expected " + expected + " but got " + actual);
    }

    @Override public String errorCode() { return "SHARED_CURRENCY_MISMATCH"; }

    @Override public String errorTitle() { return "Moedas incompatíveis"; }
}
