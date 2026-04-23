package com.wtaumaturgo.benefits.grant.domain.exception;

import com.wtaumaturgo.benefits.shared.exception.DomainException;
import com.wtaumaturgo.benefits.grant.domain.model.GrantId;

public final class CannotRenewRevokedGrantException extends DomainException {

    public CannotRenewRevokedGrantException(GrantId id) {
        super("Concessão " + id.value() + " está revogada e não pode ser renovada");
    }

    @Override public String errorCode() { return "GRANT_CANNOT_RENEW_REVOKED"; }
    @Override public String errorTitle() { return "Concessão revogada não pode ser renovada"; }
}
