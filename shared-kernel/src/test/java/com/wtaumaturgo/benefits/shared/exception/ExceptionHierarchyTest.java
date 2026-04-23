package com.wtaumaturgo.benefits.shared.exception;

import org.junit.jupiter.api.Test;

import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that all four shared-kernel exceptions extend DomainException
 * and expose the correct errorCode()/errorTitle() contract.
 */
class ExceptionHierarchyTest {

    // --- InvalidMoneyException ---

    @Test
    void invalidMoneyException_extendsDomainException() {
        InvalidMoneyException ex = new InvalidMoneyException("negative amount");
        assertThat(ex).isInstanceOf(DomainException.class);
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    void invalidMoneyException_errorCode() {
        InvalidMoneyException ex = new InvalidMoneyException("msg");
        assertThat(ex.errorCode()).isEqualTo("SHARED_INVALID_MONEY");
    }

    @Test
    void invalidMoneyException_errorTitle() {
        InvalidMoneyException ex = new InvalidMoneyException("msg");
        assertThat(ex.errorTitle()).isEqualTo("Valor monetário inválido");
    }

    @Test
    void invalidMoneyException_twoArgConstructorPreserved() {
        Throwable cause = new IllegalArgumentException("root");
        InvalidMoneyException ex = new InvalidMoneyException("wrapped", cause);
        assertThat(ex.getMessage()).isEqualTo("wrapped");
        assertThat(ex.getCause()).isSameAs(cause);
    }

    // --- InvalidCpfException ---

    @Test
    void invalidCpfException_extendsDomainException() {
        InvalidCpfException ex = new InvalidCpfException("invalid cpf");
        assertThat(ex).isInstanceOf(DomainException.class);
    }

    @Test
    void invalidCpfException_errorCode() {
        InvalidCpfException ex = new InvalidCpfException("msg");
        assertThat(ex.errorCode()).isEqualTo("SHARED_INVALID_CPF");
    }

    @Test
    void invalidCpfException_errorTitle() {
        InvalidCpfException ex = new InvalidCpfException("msg");
        assertThat(ex.errorTitle()).isEqualTo("CPF inválido");
    }

    @Test
    void invalidCpfException_twoArgConstructorPreserved() {
        Throwable cause = new IllegalArgumentException("root");
        InvalidCpfException ex = new InvalidCpfException("wrapped", cause);
        assertThat(ex.getMessage()).isEqualTo("wrapped");
        assertThat(ex.getCause()).isSameAs(cause);
    }

    // --- InvalidPeriodException ---

    @Test
    void invalidPeriodException_extendsDomainException() {
        InvalidPeriodException ex = new InvalidPeriodException("invalid period");
        assertThat(ex).isInstanceOf(DomainException.class);
    }

    @Test
    void invalidPeriodException_errorCode() {
        InvalidPeriodException ex = new InvalidPeriodException("msg");
        assertThat(ex.errorCode()).isEqualTo("SHARED_INVALID_PERIOD");
    }

    @Test
    void invalidPeriodException_errorTitle() {
        InvalidPeriodException ex = new InvalidPeriodException("msg");
        assertThat(ex.errorTitle()).isEqualTo("Período inválido");
    }

    @Test
    void invalidPeriodException_twoArgConstructorPreserved() {
        Throwable cause = new IllegalArgumentException("root");
        InvalidPeriodException ex = new InvalidPeriodException("wrapped", cause);
        assertThat(ex.getMessage()).isEqualTo("wrapped");
        assertThat(ex.getCause()).isSameAs(cause);
    }

    // --- CurrencyMismatchException ---

    @Test
    void currencyMismatchException_extendsDomainException() {
        Currency brl = Currency.getInstance("BRL");
        Currency usd = Currency.getInstance("USD");
        CurrencyMismatchException ex = new CurrencyMismatchException(brl, usd);
        assertThat(ex).isInstanceOf(DomainException.class);
    }

    @Test
    void currencyMismatchException_errorCode() {
        Currency brl = Currency.getInstance("BRL");
        Currency usd = Currency.getInstance("USD");
        CurrencyMismatchException ex = new CurrencyMismatchException(brl, usd);
        assertThat(ex.errorCode()).isEqualTo("SHARED_CURRENCY_MISMATCH");
    }

    @Test
    void currencyMismatchException_errorTitle() {
        Currency brl = Currency.getInstance("BRL");
        Currency usd = Currency.getInstance("USD");
        CurrencyMismatchException ex = new CurrencyMismatchException(brl, usd);
        assertThat(ex.errorTitle()).isEqualTo("Moedas incompatíveis");
    }

    @Test
    void currencyMismatchException_messageContainsBothCurrencies() {
        Currency brl = Currency.getInstance("BRL");
        Currency usd = Currency.getInstance("USD");
        CurrencyMismatchException ex = new CurrencyMismatchException(brl, usd);
        assertThat(ex.getMessage()).contains("BRL").contains("USD");
    }
}
