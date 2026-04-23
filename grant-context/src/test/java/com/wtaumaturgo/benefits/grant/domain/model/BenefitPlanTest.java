package com.wtaumaturgo.benefits.grant.domain.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BenefitPlanTest {

    @Test
    void shouldConstructValidBenefitPlan() {
        BenefitPlan plan = new BenefitPlan(
            UUID.randomUUID(),
            "Plano Gold",
            Set.of(MerchantCategory.FUEL_STATION, MerchantCategory.RESTAURANT)
        );
        assertThat(plan.categories()).containsExactlyInAnyOrder(
            MerchantCategory.FUEL_STATION, MerchantCategory.RESTAURANT);
    }

    @Test
    void shouldRejectNullFields() {
        UUID planId = UUID.randomUUID();
        Set<MerchantCategory> cats = Set.of(MerchantCategory.PHARMACY);

        assertThatThrownBy(() -> new BenefitPlan(null, "x", cats))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new BenefitPlan(planId, null, cats))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new BenefitPlan(planId, "x", null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldDefensivelyCopyCategories() {
        Set<MerchantCategory> mutable = new HashSet<>();
        mutable.add(MerchantCategory.SUPERMARKET);

        BenefitPlan plan = new BenefitPlan(UUID.randomUUID(), "Plano Silver", mutable);
        mutable.add(MerchantCategory.PHARMACY);

        assertThat(plan.categories()).containsExactly(MerchantCategory.SUPERMARKET);
        assertThatThrownBy(() -> plan.categories().add(MerchantCategory.FUEL_STATION))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}
