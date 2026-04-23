package com.wtaumaturgo.benefits.grant.domain.model;

import com.wtaumaturgo.benefits.shared.domain.model.DomainEvent;
import com.wtaumaturgo.benefits.shared.domain.model.Period;

import java.time.Instant;
import java.util.UUID;

/** Emitted by Grant.renew(newValidity). */
public record GrantRenewed(
    UUID eventId,
    Instant occurredOn,
    GrantId grantId,
    Period newValidity
) implements DomainEvent {
}
