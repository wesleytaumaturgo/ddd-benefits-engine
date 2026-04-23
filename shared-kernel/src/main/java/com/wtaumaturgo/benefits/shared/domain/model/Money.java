package com.wtaumaturgo.benefits.shared.domain.model;

import com.wtaumaturgo.benefits.shared.exception.CurrencyMismatchException;
import com.wtaumaturgo.benefits.shared.exception.InvalidMoneyException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Monetary value with explicit currency. Amounts are normalized to scale 2 using
 * {@link RoundingMode#HALF_EVEN} (Banker's Rounding) to eliminate systematic bias
 * across repeated arithmetic operations.
 *
 * <p>Arithmetic operations require both operands to share the same currency;
 * otherwise a {@link CurrencyMismatchException} is raised. Negative amounts are
 * rejected at construction via {@link InvalidMoneyException}. The
 * {@link #subtract(Money)} operation may still produce a negative result — it is
 * the aggregate's responsibility to guard against over-subtraction.</p>
 */
public final class Money {

    private final BigDecimal amount;
    private final Currency currency;

    private Money(BigDecimal amount, Currency currency, boolean allowNegative) {
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(currency, "currency must not be null");
        if (!allowNegative && amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidMoneyException("amount must not be negative: " + amount);
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);
        this.currency = currency;
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency, false);
    }

    public BigDecimal amount() {
        return amount;
    }

    public Currency currency() {
        return currency;
    }

    public Money add(Money other) {
        assertSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency, false);
    }

    /**
     * Subtracts {@code other} from {@code this}. The resulting Money may be
     * negative — guarding against that is the aggregate's responsibility
     * (e.g. Wallet.debit rejecting over-subtraction).
     */
    public Money subtract(Money other) {
        assertSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency, true);
    }

    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isLessThan(Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    public boolean isLessThanOrEqualTo(Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount) <= 0;
    }

    public boolean isGreaterThan(Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isGreaterThanOrEqualTo(Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount) >= 0;
    }

    private void assertSameCurrency(Money other) {
        Objects.requireNonNull(other, "other must not be null");
        if (!this.currency.equals(other.currency)) {
            throw new CurrencyMismatchException(this.currency, other.currency);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money other)) return false;
        return amount.compareTo(other.amount) == 0 && currency.equals(other.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros(), currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency.getCurrencyCode();
    }
}
