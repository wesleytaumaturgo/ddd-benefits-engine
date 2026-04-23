package com.wtaumaturgo.benefits.grant.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Strongly-typed identifier for the Grant aggregate (D-07). Prevents accidental
 * cross-context UUID swaps at compile time.
 */
public record GrantId(UUID value) {
    public GrantId {
        Objects.requireNonNull(value, "GrantId value must not be null");
    }
}
