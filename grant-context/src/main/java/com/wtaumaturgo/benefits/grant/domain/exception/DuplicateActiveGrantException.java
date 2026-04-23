package com.wtaumaturgo.benefits.grant.domain.exception;

import com.wtaumaturgo.benefits.shared.exception.DomainException;

import java.util.UUID;

public final class DuplicateActiveGrantException extends DomainException {

    public DuplicateActiveGrantException(UUID beneficiaryId, UUID planId) {
        super("Beneficiário " + beneficiaryId + " já possui concessão ativa do plano " + planId);
    }

    @Override public String errorCode() { return "GRANT_DUPLICATE_ACTIVE"; }
    @Override public String errorTitle() { return "Concessão duplicada"; }
}
