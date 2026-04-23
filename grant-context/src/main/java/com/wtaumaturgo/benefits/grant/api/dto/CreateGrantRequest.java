package com.wtaumaturgo.benefits.grant.api.dto;

import com.wtaumaturgo.benefits.grant.application.dto.CreateGrantCommand;
import com.wtaumaturgo.benefits.grant.domain.model.MerchantCategory;
import com.wtaumaturgo.benefits.shared.domain.model.Cycle;
import com.wtaumaturgo.benefits.shared.domain.model.Money;
import com.wtaumaturgo.benefits.shared.domain.model.Period;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Set;
import java.util.UUID;

/**
 * REST payload for POST /grants. Flat primitives on the wire — VO construction
 * happens in {@link #toCommand()} so that any {@code InvalidMoneyException},
 * {@code InvalidPeriodException}, etc. (all extend DomainException — Plan 01)
 * surface as HTTP 422 via GlobalExceptionHandler.
 */
public record CreateGrantRequest(
    UUID beneficiaryId,
    UUID planId,
    String planName,
    Set<MerchantCategory> categories,
    BigDecimal amountValue,
    String amountCurrency,
    LocalDate validFrom,
    LocalDate validUntil,
    int cycleYear,
    int cycleMonth
) {
    public CreateGrantCommand toCommand() {
        return new CreateGrantCommand(
            beneficiaryId,
            planId,
            planName,
            categories,
            Money.of(amountValue, Currency.getInstance(amountCurrency)),
            Period.of(validFrom, validUntil),
            Cycle.of(cycleYear, cycleMonth)
        );
    }
}
