package com.wtaumaturgo.benefits.grant.domain.repository;

import com.wtaumaturgo.benefits.grant.domain.model.Grant;
import com.wtaumaturgo.benefits.grant.domain.model.GrantId;

import java.util.Optional;
import java.util.UUID;

/**
 * Pure domain repository — zero Spring imports. Implementations live in
 * grant.infrastructure.persistence (Plan 04).
 */
public interface GrantRepository {

    /** Idempotent upsert — maps to INSERT or UPDATE based on {@link Grant#id()}. */
    void save(Grant grant);

    Optional<Grant> findById(GrantId id);

    /** Enforces GRANT-02 at query time. Status compared as {@code "ACTIVE"} string. */
    boolean existsActiveByBeneficiaryAndPlan(UUID beneficiaryId, UUID planId);
}
