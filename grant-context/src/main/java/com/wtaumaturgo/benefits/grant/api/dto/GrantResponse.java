package com.wtaumaturgo.benefits.grant.api.dto;

import com.wtaumaturgo.benefits.grant.domain.model.Grant;
import com.wtaumaturgo.benefits.grant.domain.model.MerchantCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

/** Display-ready projection of a Grant. Never expose the aggregate directly. */
public record GrantResponse(
    UUID grantId,
    UUID beneficiaryId,
    UUID planId,
    String planName,
    Set<MerchantCategory> categories,
    BigDecimal amountValue,
    String amountCurrency,
    LocalDate validFrom,
    LocalDate validUntil,
    int cycleYear,
    int cycleMonth,
    String status
) {
    public static GrantResponse from(Grant grant) {
        return new GrantResponse(
            grant.id().value(),
            grant.beneficiaryId(),
            grant.plan().planId(),
            grant.plan().planName(),
            grant.plan().categories(),
            grant.amount().amount(),
            grant.amount().currency().getCurrencyCode(),
            grant.validity().from(),
            grant.validity().until(),
            grant.cycle().year(),
            grant.cycle().month(),
            grant.status().name()
        );
    }
}
