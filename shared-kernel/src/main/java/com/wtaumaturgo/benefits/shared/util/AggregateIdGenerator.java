package com.wtaumaturgo.benefits.shared.util;

import com.github.f4b6a3.uuid.UuidCreator;

import java.util.UUID;

/**
 * Generates UUID v7 (time-ordered) aggregate IDs.
 *
 * <p>All aggregates call this — never import {@code UuidCreator} directly in
 * domain classes. Centralizing ID generation here keeps the domain layer free
 * of third-party library imports and makes it trivial to swap the UUID strategy
 * in the future.</p>
 */
public final class AggregateIdGenerator {

    private AggregateIdGenerator() {
        // utility class — no instances
    }

    public static UUID newId() {
        return UuidCreator.getTimeOrderedEpoch();
    }
}
