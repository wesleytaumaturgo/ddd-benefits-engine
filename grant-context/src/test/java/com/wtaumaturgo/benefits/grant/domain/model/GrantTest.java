package com.wtaumaturgo.benefits.grant.domain.model;

import com.wtaumaturgo.benefits.grant.domain.exception.CannotRenewRevokedGrantException;
import com.wtaumaturgo.benefits.grant.domain.exception.GrantAlreadyRevokedException;
import com.wtaumaturgo.benefits.shared.domain.model.Cycle;
import com.wtaumaturgo.benefits.shared.domain.model.DomainEvent;
import com.wtaumaturgo.benefits.shared.domain.model.Money;
import com.wtaumaturgo.benefits.shared.domain.model.Period;
import com.wtaumaturgo.benefits.shared.exception.InvalidPeriodException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GrantTest {

    private static final Currency BRL = Currency.getInstance("BRL");
    private static final LocalDate APR_01 = LocalDate.of(2026, 4, 1);
    private static final LocalDate MAY_01 = LocalDate.of(2026, 5, 1);
    private static final LocalDate JUN_01 = LocalDate.of(2026, 6, 1);

    private Grant newGrant() {
        return Grant.create(
            UUID.randomUUID(),
            new BenefitPlan(UUID.randomUUID(), "Plano", Set.of(MerchantCategory.FUEL_STATION)),
            Money.of(new BigDecimal("150.00"), BRL),
            Period.of(APR_01, MAY_01),
            Cycle.of(2026, 4)
        );
    }

    @Test
    void createShouldEmitGrantCreated() {
        Grant grant = newGrant();
        List<DomainEvent> events = grant.pullDomainEvents();

        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(GrantCreated.class);
    }

    @Test
    void grantCreatedPayload() {
        UUID beneficiaryId = UUID.randomUUID();
        UUID planId = UUID.randomUUID();
        Money amount = Money.of(new BigDecimal("200.00"), BRL);
        Cycle cycle = Cycle.of(2026, 5);
        Set<MerchantCategory> cats = Set.of(MerchantCategory.RESTAURANT, MerchantCategory.PHARMACY);

        Grant grant = Grant.create(
            beneficiaryId,
            new BenefitPlan(planId, "Plano Premium", cats),
            amount,
            Period.of(MAY_01, JUN_01),
            cycle
        );

        List<DomainEvent> events = grant.pullDomainEvents();
        assertThat(events).hasSize(1);
        GrantCreated event = (GrantCreated) events.get(0);
        assertThat(event.grantId()).isEqualTo(grant.id());
        assertThat(event.beneficiaryId()).isEqualTo(beneficiaryId);
        assertThat(event.planId()).isEqualTo(planId);
        assertThat(event.amount()).isEqualTo(amount);
        assertThat(event.cycle()).isEqualTo(cycle);
        assertThat(event.merchantCategories()).isEqualTo(cats);
    }

    @Test
    void invalidPeriodShouldThrow() {
        // Period.of enforces from < until; Grant.create delegates to it.
        assertThatThrownBy(() -> Period.of(MAY_01, APR_01))
            .isInstanceOf(InvalidPeriodException.class);
    }

    @Test
    void revokeShouldEmitGrantRevoked() {
        Grant grant = newGrant();
        grant.pullDomainEvents();   // drain GrantCreated

        grant.revoke();

        List<DomainEvent> events = grant.pullDomainEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(GrantRevoked.class);
        assertThat(((GrantRevoked) events.get(0)).grantId()).isEqualTo(grant.id());
        assertThat(grant.status()).isEqualTo(GrantStatus.REVOKED);
    }

    @Test
    void revokingTwiceShouldThrow() {
        Grant grant = newGrant();
        grant.revoke();

        assertThatThrownBy(grant::revoke).isInstanceOf(GrantAlreadyRevokedException.class);
    }

    @Test
    void renewShouldEmitGrantRenewed() {
        Grant grant = newGrant();
        grant.pullDomainEvents();

        Period newValidity = Period.of(MAY_01, JUN_01);
        grant.renew(newValidity);

        List<DomainEvent> events = grant.pullDomainEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(GrantRenewed.class);
        assertThat(((GrantRenewed) events.get(0)).newValidity()).isEqualTo(newValidity);
        assertThat(grant.validity()).isEqualTo(newValidity);
    }

    @Test
    void renewingRevokedGrantShouldThrow() {
        Grant grant = newGrant();
        grant.revoke();

        assertThatThrownBy(() -> grant.renew(Period.of(MAY_01, JUN_01)))
            .isInstanceOf(CannotRenewRevokedGrantException.class);
    }

    @Test
    void pullShouldDrainList() {
        Grant grant = newGrant();
        List<DomainEvent> first = grant.pullDomainEvents();
        List<DomainEvent> second = grant.pullDomainEvents();

        assertThat(first).hasSize(1);
        assertThat(second).isEmpty();
    }

    @Test
    void pullReturnedListShouldBeImmutable() {
        Grant grant = newGrant();
        List<DomainEvent> events = grant.pullDomainEvents();

        assertThatThrownBy(() -> events.add(null))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}
