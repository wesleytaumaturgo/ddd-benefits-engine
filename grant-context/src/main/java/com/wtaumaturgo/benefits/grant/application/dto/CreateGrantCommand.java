package com.wtaumaturgo.benefits.grant.application.dto;

import com.wtaumaturgo.benefits.grant.domain.model.MerchantCategory;
import com.wtaumaturgo.benefits.shared.domain.model.Cycle;
import com.wtaumaturgo.benefits.shared.domain.model.Money;
import com.wtaumaturgo.benefits.shared.domain.model.Period;

import java.util.Set;
import java.util.UUID;

/** Input for CreateGrantUseCase. Records are immutable by construction. */
public record CreateGrantCommand(
    UUID beneficiaryId,
    UUID planId,
    String planName,
    Set<MerchantCategory> categories,
    Money amount,
    Period validity,
    Cycle cycle
) {
}
