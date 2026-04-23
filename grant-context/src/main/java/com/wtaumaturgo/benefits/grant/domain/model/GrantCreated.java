package com.wtaumaturgo.benefits.grant.domain.model;

import com.wtaumaturgo.benefits.shared.domain.model.Cycle;
import com.wtaumaturgo.benefits.shared.domain.model.DomainEvent;
import com.wtaumaturgo.benefits.shared.domain.model.Money;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/** Emitted by Grant.create(...). Payload defined by GRANT-06. */
public record GrantCreated(
    UUID eventId,
    Instant occurredOn,
    GrantId grantId,
    UUID beneficiaryId,
    UUID planId,
    Money amount,
    Cycle cycle,
    Set<MerchantCategory> merchantCategories
) implements DomainEvent {
}
