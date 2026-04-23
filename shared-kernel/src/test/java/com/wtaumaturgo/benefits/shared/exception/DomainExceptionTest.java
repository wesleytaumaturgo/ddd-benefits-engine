package com.wtaumaturgo.benefits.shared.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies DomainException abstract class contract.
 */
class DomainExceptionTest {

    /** Minimal concrete subclass for testing purposes. */
    static class FakeDomainException extends DomainException {

        FakeDomainException(String message) {
            super(message);
        }

        FakeDomainException(String message, Throwable cause) {
            super(message, cause);
        }

        @Override
        public String errorCode() {
            return "FAKE_ERROR_CODE";
        }

        @Override
        public String errorTitle() {
            return "Erro fictício";
        }
    }

    @Test
    void domainException_isRuntimeException() {
        FakeDomainException ex = new FakeDomainException("test");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    void errorCode_returnsExpectedConstant() {
        FakeDomainException ex = new FakeDomainException("test");
        assertThat(ex.errorCode()).isEqualTo("FAKE_ERROR_CODE");
    }

    @Test
    void errorTitle_returnsExpectedTitle() {
        FakeDomainException ex = new FakeDomainException("test");
        assertThat(ex.errorTitle()).isEqualTo("Erro fictício");
    }

    @Test
    void constructor_withCause_preservesMessage() {
        Throwable cause = new IllegalArgumentException("root");
        FakeDomainException ex = new FakeDomainException("wrapped", cause);
        assertThat(ex.getMessage()).isEqualTo("wrapped");
        assertThat(ex.getCause()).isSameAs(cause);
    }
}
