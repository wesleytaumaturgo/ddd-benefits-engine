package com.wtaumaturgo.benefits.grant.domain.exception;

import com.wtaumaturgo.benefits.shared.exception.DomainException;

public final class InvalidGrantPeriodException extends DomainException {

    public InvalidGrantPeriodException(String message) {
        super(message);
    }

    public InvalidGrantPeriodException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override public String errorCode() { return "GRANT_INVALID_PERIOD"; }
    @Override public String errorTitle() { return "Período de vigência inválido"; }
}
