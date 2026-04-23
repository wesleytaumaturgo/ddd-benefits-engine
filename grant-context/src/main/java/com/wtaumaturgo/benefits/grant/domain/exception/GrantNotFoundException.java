package com.wtaumaturgo.benefits.grant.domain.exception;

import com.wtaumaturgo.benefits.shared.exception.DomainException;
import com.wtaumaturgo.benefits.grant.domain.model.GrantId;

public final class GrantNotFoundException extends DomainException {

    public GrantNotFoundException(GrantId id) {
        super("Concessão não encontrada: " + id.value());
    }

    @Override public String errorCode() { return "GRANT_NOT_FOUND"; }
    @Override public String errorTitle() { return "Concessão não encontrada"; }
}
