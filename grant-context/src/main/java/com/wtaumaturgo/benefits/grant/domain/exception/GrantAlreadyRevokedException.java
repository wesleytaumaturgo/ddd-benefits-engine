package com.wtaumaturgo.benefits.grant.domain.exception;

import com.wtaumaturgo.benefits.shared.exception.DomainException;
import com.wtaumaturgo.benefits.grant.domain.model.GrantId;

public final class GrantAlreadyRevokedException extends DomainException {

    public GrantAlreadyRevokedException(GrantId id) {
        super("Concessão " + id.value() + " já está revogada");
    }

    @Override public String errorCode() { return "GRANT_ALREADY_REVOKED"; }
    @Override public String errorTitle() { return "Concessão já revogada"; }
}
