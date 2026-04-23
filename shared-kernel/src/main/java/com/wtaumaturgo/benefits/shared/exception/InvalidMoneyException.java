package com.wtaumaturgo.benefits.shared.exception;

/**
 * Thrown when a Money value object cannot be constructed —
 * for example, when the amount is negative.
 */
public class InvalidMoneyException extends DomainException {

    public InvalidMoneyException(String message) {
        super(message);
    }

    public InvalidMoneyException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override public String errorCode() { return "SHARED_INVALID_MONEY"; }

    @Override public String errorTitle() { return "Valor monetário inválido"; }
}
