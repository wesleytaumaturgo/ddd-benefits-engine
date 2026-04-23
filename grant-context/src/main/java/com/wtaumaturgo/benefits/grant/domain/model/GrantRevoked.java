package com.wtaumaturgo.benefits.grant.domain.model;

import com.wtaumaturgo.benefits.shared.domain.model.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/** Emitted by Grant.revoke(). */
public record GrantRevoked(
    UUID eventId,
    Instant occurredOn,
    GrantId grantId
) implements DomainEvent {
}
