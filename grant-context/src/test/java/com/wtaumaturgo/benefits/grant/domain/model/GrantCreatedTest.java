package com.wtaumaturgo.benefits.grant.domain.model;

import com.wtaumaturgo.benefits.shared.domain.model.Cycle;
import com.wtaumaturgo.benefits.shared.domain.model.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GrantCreatedTest {

    @Test
    void shouldEqualWhenAllFieldsMatch() {
        UUID eventId = UUID.randomUUID();
        Instant occurredOn = Instant.parse("2026-04-23T12:00:00Z");
        GrantId grantId = new GrantId(UUID.randomUUID());
        UUID beneficiaryId = UUID.randomUUID();
        UUID planId = UUID.randomUUID();
        Money amount = Money.of(new BigDecimal("100.00"), Currency.getInstance("BRL"));
        Cycle cycle = Cycle.of(2026, 4);
        Set<MerchantCategory> cats = Set.of(MerchantCategory.FUEL_STATION);

        GrantCreated a = new GrantCreated(eventId, occurredOn, grantId, beneficiaryId, planId, amount, cycle, cats);
        GrantCreated b = new GrantCreated(eventId, occurredOn, grantId, beneficiaryId, planId, amount, cycle, cats);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}
