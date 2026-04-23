package com.wtaumaturgo.benefits.grant.domain.model;

import com.wtaumaturgo.benefits.grant.domain.exception.CannotRenewRevokedGrantException;
import com.wtaumaturgo.benefits.grant.domain.exception.GrantAlreadyRevokedException;
import com.wtaumaturgo.benefits.shared.domain.model.AggregateRoot;
import com.wtaumaturgo.benefits.shared.domain.model.Cycle;
import com.wtaumaturgo.benefits.shared.domain.model.DomainEvent;
import com.wtaumaturgo.benefits.shared.domain.model.Money;
import com.wtaumaturgo.benefits.shared.domain.model.Period;
import com.wtaumaturgo.benefits.shared.util.AggregateIdGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate root for a benefit grant (Concessão). Immutable from the outside —
 * every state change (revoke / renew) goes through a method that validates the
 * transition AND records a domain event. Per D-08, this class holds its own
 * {@code List<DomainEvent>} and implements {@link AggregateRoot#pullDomainEvents()}
 * via the copy-before-clear drain pattern (Pitfall 3).
 *
 * <p>Zero Spring / JPA dependencies — enforced by {@code LayeredArchitectureTest}.</p>
 */
public final class Grant implements AggregateRoot<GrantId> {

    private final GrantId id;
    private final UUID beneficiaryId;
    private final BenefitPlan plan;
    private final Money amount;
    private final Cycle cycle;
    private Period validity;          // mutable on renew()
    private GrantStatus status;       // mutable on revoke()
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * Factory for a brand-new Grant. Always records a {@link GrantCreated} event
     * carrying the GRANT-06 payload. ID is generated via {@link AggregateIdGenerator}
     * (UUID v7, FUND-03).
     */
    public static Grant create(UUID beneficiaryId,
                               BenefitPlan plan,
                               Money amount,
                               Period validity,
                               Cycle cycle) {
        GrantId newId = new GrantId(AggregateIdGenerator.newId());
        Grant g = new Grant(newId, beneficiaryId, plan, amount, validity, cycle, GrantStatus.ACTIVE);
        g.domainEvents.add(new GrantCreated(
            UUID.randomUUID(),
            Instant.now(),
            newId,
            beneficiaryId,
            plan.planId(),
            amount,
            cycle,
            plan.categories()
        ));
        return g;
    }

    /**
     * Reconstitution factory used by repository implementations (Plan 04). Does
     * NOT emit events — the aggregate already existed in storage. Exposed as
     * {@code public} for simplicity; callers other than repositories should not
     * invoke this (documented-only constraint).
     */
    public static Grant rehydrate(GrantId id,
                                  UUID beneficiaryId,
                                  BenefitPlan plan,
                                  Money amount,
                                  Period validity,
                                  Cycle cycle,
                                  GrantStatus status) {
        return new Grant(id, beneficiaryId, plan, amount, validity, cycle, status);
    }

    private Grant(GrantId id,
                  UUID beneficiaryId,
                  BenefitPlan plan,
                  Money amount,
                  Period validity,
                  Cycle cycle,
                  GrantStatus status) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.beneficiaryId = Objects.requireNonNull(beneficiaryId, "beneficiaryId must not be null");
        this.plan = Objects.requireNonNull(plan, "plan must not be null");
        this.amount = Objects.requireNonNull(amount, "amount must not be null");
        this.validity = Objects.requireNonNull(validity, "validity must not be null");
        this.cycle = Objects.requireNonNull(cycle, "cycle must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    /**
     * Transitions status to REVOKED and records {@link GrantRevoked}. Throws
     * {@link GrantAlreadyRevokedException} if already revoked (GRANT-04).
     */
    public void revoke() {
        if (status == GrantStatus.REVOKED) {
            throw new GrantAlreadyRevokedException(id);
        }
        this.status = GrantStatus.REVOKED;
        domainEvents.add(new GrantRevoked(UUID.randomUUID(), Instant.now(), id));
    }

    /**
     * Updates validity to {@code newValidity} and records {@link GrantRenewed}.
     * Throws {@link CannotRenewRevokedGrantException} if status is REVOKED (GRANT-05).
     */
    public void renew(Period newValidity) {
        if (status == GrantStatus.REVOKED) {
            throw new CannotRenewRevokedGrantException(id);
        }
        this.validity = Objects.requireNonNull(newValidity, "newValidity must not be null");
        domainEvents.add(new GrantRenewed(UUID.randomUUID(), Instant.now(), id, newValidity));
    }

    @Override public GrantId id() { return id; }

    /**
     * Copy-before-clear drain (Pitfall 3). Returns an immutable snapshot of all
     * events recorded since the last drain and clears the internal buffer.
     * Calling a second time returns an empty list.
     */
    @Override
    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> snapshot = List.copyOf(domainEvents);
        domainEvents.clear();
        return snapshot;
    }

    // Package-private accessors for the JPA mapper (grant-context.infrastructure.persistence).
    UUID beneficiaryId() { return beneficiaryId; }
    BenefitPlan plan() { return plan; }
    Money amount() { return amount; }
    Period validity() { return validity; }
    Cycle cycle() { return cycle; }
    GrantStatus status() { return status; }
}
