package com.wtaumaturgo.benefits.shared.domain.model;

import com.wtaumaturgo.benefits.shared.exception.InvalidCpfException;

import java.util.Objects;

/**
 * Brazilian taxpayer identifier (Cadastro de Pessoas Fisicas).
 *
 * <p>Immutable value object. Accepts CPF strings with or without punctuation and
 * normalizes them to digits-only internal representation. Validates:</p>
 * <ol>
 *     <li>Length is exactly 11 digits after normalization.</li>
 *     <li>Sequence is not composed of a single repeating digit.</li>
 *     <li>Both mod-11 check digits match the Receita Federal algorithm.</li>
 * </ol>
 *
 * <p>Any failure raises {@link InvalidCpfException} at construction, so an
 * invalid CPF instance cannot exist.</p>
 */
public final class CPF {

    private static final int CPF_LENGTH = 11;

    private final String digits;

    public CPF(String value) {
        Objects.requireNonNull(value, "CPF must not be null");
        String normalized = value.replaceAll("[^\\d]", "");
        if (normalized.length() != CPF_LENGTH) {
            throw new InvalidCpfException("CPF must have 11 digits: " + value);
        }
        if (isRepeatingSequence(normalized)) {
            throw new InvalidCpfException("CPF must not be a repeating sequence: " + value);
        }
        if (!isValidCheckDigits(normalized)) {
            throw new InvalidCpfException("CPF has invalid check digits: " + value);
        }
        this.digits = normalized;
    }

    public String value() {
        return digits;
    }

    public String formatted() {
        return digits.substring(0, 3) + "." + digits.substring(3, 6) + "."
            + digits.substring(6, 9) + "-" + digits.substring(9);
    }

    private static boolean isRepeatingSequence(String digits) {
        return digits.chars().distinct().count() == 1;
    }

    private static boolean isValidCheckDigits(String digits) {
        return checkDigit(digits, 9) == (digits.charAt(9) - '0')
            && checkDigit(digits, 10) == (digits.charAt(10) - '0');
    }

    private static int checkDigit(String digits, int length) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += (digits.charAt(i) - '0') * (length + 1 - i);
        }
        int remainder = 11 - (sum % 11);
        return remainder >= 10 ? 0 : remainder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CPF other)) return false;
        return digits.equals(other.digits);
    }

    @Override
    public int hashCode() {
        return digits.hashCode();
    }

    @Override
    public String toString() {
        return formatted();
    }
}
