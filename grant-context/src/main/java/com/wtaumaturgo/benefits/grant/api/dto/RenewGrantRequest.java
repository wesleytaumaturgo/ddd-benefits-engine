package com.wtaumaturgo.benefits.grant.api.dto;

import com.wtaumaturgo.benefits.grant.application.dto.RenewGrantCommand;
import com.wtaumaturgo.benefits.grant.domain.model.GrantId;
import com.wtaumaturgo.benefits.shared.domain.model.Period;

import java.time.LocalDate;

/** REST payload for PUT /grants/{id}/renew. */
public record RenewGrantRequest(LocalDate validFrom, LocalDate validUntil) {
    public RenewGrantCommand toCommand(GrantId id) {
        return new RenewGrantCommand(id, Period.of(validFrom, validUntil));
    }
}
