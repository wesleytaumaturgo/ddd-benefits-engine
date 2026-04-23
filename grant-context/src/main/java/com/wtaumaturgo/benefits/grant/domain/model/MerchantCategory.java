package com.wtaumaturgo.benefits.grant.domain.model;

/**
 * Merchant category allowed by a BenefitPlan. Scoped to grant-context —
 * redemption-context will declare its own copy in Phase 4 (Pitfall 7 in RESEARCH).
 */
public enum MerchantCategory {
    FUEL_STATION,
    RESTAURANT,
    SUPERMARKET,
    PHARMACY
}
