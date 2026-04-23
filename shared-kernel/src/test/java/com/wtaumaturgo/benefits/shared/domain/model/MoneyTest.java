package com.wtaumaturgo.benefits.shared.domain.model;

import com.wtaumaturgo.benefits.shared.exception.CurrencyMismatchException;
import com.wtaumaturgo.benefits.shared.exception.InvalidMoneyException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    private static final Currency BRL = Currency.getInstance("BRL");
    private static final Currency USD = Currency.getInstance("USD");

    @Test
    void shouldConstructValidMoney() {
        assertThatCode(() -> Money.of(new BigDecimal("10.00"), BRL)).doesNotThrowAnyException();
    }

    @Test
    void shouldRejectNegativeAmount() {
        assertThatThrownBy(() -> Money.of(new BigDecimal("-1.00"), BRL))
            .isInstanceOf(InvalidMoneyException.class);
    }

    @Test
    void addSameCurrencyShouldSucceed() {
        Money ten = Money.of(new BigDecimal("10.00"), BRL);
        Money five = Money.of(new BigDecimal("5.00"), BRL);
        assertThat(ten.add(five).amount()).isEqualByComparingTo(new BigDecimal("15.00"));
    }

    @Test
    void addDifferentCurrencyShouldThrow() {
        Money brl = Money.of(new BigDecimal("10.00"), BRL);
        Money usd = Money.of(new BigDecimal("5.00"), USD);
        assertThatThrownBy(() -> brl.add(usd))
            .isInstanceOf(CurrencyMismatchException.class);
    }

    @Test
    void isGreaterThanShouldCompareCorrectly() {
        Money ten = Money.of(new BigDecimal("10.00"), BRL);
        Money five = Money.of(new BigDecimal("5.00"), BRL);
        assertThat(ten.isGreaterThan(five)).isTrue();
        assertThat(five.isGreaterThan(ten)).isFalse();
    }

    @Test
    void shouldRoundHalfToEvenDown() {
        // 1.005 -> 1.00: digit before 5 is 0 (even), stays — HALF_EVEN
        Money money = Money.of(new BigDecimal("1.005"), BRL);
        assertThat(money.amount()).isEqualByComparingTo(new BigDecimal("1.00"));
    }

    @Test
    void shouldRoundHalfToEvenUp() {
        // 1.015 -> 1.02: digit before 5 is 1 (odd), rounds up to 2 — HALF_EVEN
        Money money = Money.of(new BigDecimal("1.015"), BRL);
        assertThat(money.amount()).isEqualByComparingTo(new BigDecimal("1.02"));
    }

    // --- isNegative() ---

    @Test
    void isNegativeShouldBeFalseForZero() {
        assertThat(Money.of(BigDecimal.ZERO, BRL).isNegative()).isFalse();
    }

    @Test
    void isNegativeShouldBeFalseForPositive() {
        assertThat(Money.of(new BigDecimal("5.00"), BRL).isNegative()).isFalse();
    }

    @Test
    void isNegativeShouldBeTrueAfterSubtractExceedsReceiver() {
        Money five = Money.of(new BigDecimal("5.00"), BRL);
        Money ten  = Money.of(new BigDecimal("10.00"), BRL);
        assertThat(five.subtract(ten).isNegative()).isTrue();
    }

    // --- isLessThan() ---

    @Test
    void isLessThanShouldReturnTrueWhenAmountIsSmaller() {
        Money five = Money.of(new BigDecimal("5.00"), BRL);
        Money ten  = Money.of(new BigDecimal("10.00"), BRL);
        assertThat(five.isLessThan(ten)).isTrue();
    }

    @Test
    void isLessThanShouldReturnFalseWhenAmountIsLarger() {
        Money five = Money.of(new BigDecimal("5.00"), BRL);
        Money ten  = Money.of(new BigDecimal("10.00"), BRL);
        assertThat(ten.isLessThan(five)).isFalse();
    }

    @Test
    void isLessThanShouldReturnFalseWhenAmountsAreEqual() {
        Money five      = Money.of(new BigDecimal("5.00"), BRL);
        Money fiveCopy  = Money.of(new BigDecimal("5.00"), BRL);
        assertThat(five.isLessThan(fiveCopy)).isFalse();
    }

    @Test
    void isLessThanShouldThrowWhenCurrenciesDiffer() {
        Money fiveBRL = Money.of(new BigDecimal("5.00"), BRL);
        Money fiveUSD = Money.of(new BigDecimal("5.00"), USD);
        assertThatThrownBy(() -> fiveBRL.isLessThan(fiveUSD))
            .isInstanceOf(CurrencyMismatchException.class);
    }

    // --- isLessThanOrEqualTo() ---

    @Test
    void isLessThanOrEqualToShouldReturnTrueWhenAmountIsSmaller() {
        Money five = Money.of(new BigDecimal("5.00"), BRL);
        Money ten  = Money.of(new BigDecimal("10.00"), BRL);
        assertThat(five.isLessThanOrEqualTo(ten)).isTrue();
    }

    @Test
    void isLessThanOrEqualToShouldReturnTrueWhenAmountsAreEqual() {
        Money five      = Money.of(new BigDecimal("5.00"), BRL);
        Money fiveCopy  = Money.of(new BigDecimal("5.00"), BRL);
        assertThat(five.isLessThanOrEqualTo(fiveCopy)).isTrue();
    }

    @Test
    void isLessThanOrEqualToShouldReturnFalseWhenAmountIsLarger() {
        Money five = Money.of(new BigDecimal("5.00"), BRL);
        Money ten  = Money.of(new BigDecimal("10.00"), BRL);
        assertThat(ten.isLessThanOrEqualTo(five)).isFalse();
    }

    @Test
    void isLessThanOrEqualToShouldThrowWhenCurrenciesDiffer() {
        Money fiveBRL = Money.of(new BigDecimal("5.00"), BRL);
        Money fiveUSD = Money.of(new BigDecimal("5.00"), USD);
        assertThatThrownBy(() -> fiveBRL.isLessThanOrEqualTo(fiveUSD))
            .isInstanceOf(CurrencyMismatchException.class);
    }
}
