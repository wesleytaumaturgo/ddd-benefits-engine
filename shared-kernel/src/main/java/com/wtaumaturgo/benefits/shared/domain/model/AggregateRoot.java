package com.wtaumaturgo.benefits.shared.domain.model;

import java.util.List;

/**
 * Marker interface implemented by every aggregate root in the project
 * (Grant, Wallet, Redemption). Holds no state — each aggregate owns its own
 * mutable {@code List<DomainEvent>} and drains it via {@link #pullDomainEvents()}.
 *
 * <p>Rationale: Spring Data's {@code AbstractAggregateRoot} pulls
 * {@code spring-data-commons} into the domain layer, violating CLAUDE.md
 * non-negotiable rule #1 (domain must be pure Java). This interface keeps
 * the contract while leaving the event-list plumbing visible and explicit
 * in each aggregate — no framework magic.</p>
 */
public interface AggregateRoot<ID> {

    /** @return the strongly-typed identifier of this aggregate. */
    ID id();

    /**
     * Returns an immutable snapshot of events recorded since the last call
     * and clears the internal buffer atomically (copy-before-clear).
     * Callers (application-layer use cases) publish the returned events
     * after {@code repository.save()} completes.
     */
    List<DomainEvent> pullDomainEvents();
}
