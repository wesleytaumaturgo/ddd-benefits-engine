package com.wtaumaturgo.benefits.grant.domain.model;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Embedded value object on Grant (D-02). Categories set is defensively copied
 * via {@link Set#copyOf(java.util.Collection)} to guarantee immutability even
 * if the caller retains a mutable reference.
 */
public record BenefitPlan(UUID planId, String planName, Set<MerchantCategory> categories) {
    public BenefitPlan {
        Objects.requireNonNull(planId, "planId must not be null");
        Objects.requireNonNull(planName, "planName must not be null");
        Objects.requireNonNull(categories, "categories must not be null");
        categories = Set.copyOf(categories);
    }
}
