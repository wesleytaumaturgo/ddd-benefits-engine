package com.wtaumaturgo.benefits.shared.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Marker interface for all domain events emitted by aggregates.
 * Aggregates collect events internally; application layer publishes after repository.save().
 */
public interface DomainEvent {

    UUID eventId();

    Instant occurredOn();
}
