package com.wtaumaturgo.benefits.grant.infrastructure.persistence;

import com.wtaumaturgo.benefits.grant.domain.model.BenefitPlan;
import com.wtaumaturgo.benefits.grant.domain.model.Grant;
import com.wtaumaturgo.benefits.grant.domain.model.GrantId;
import com.wtaumaturgo.benefits.grant.domain.model.GrantStatus;
import com.wtaumaturgo.benefits.grant.domain.repository.GrantRepository;
import com.wtaumaturgo.benefits.shared.domain.model.Cycle;
import com.wtaumaturgo.benefits.shared.domain.model.Money;
import com.wtaumaturgo.benefits.shared.domain.model.Period;
import org.springframework.stereotype.Repository;

import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

/**
 * Bridges the pure-domain {@link GrantRepository} interface to Spring Data JPA.
 * Manual mapping both directions — no MapStruct, no reflection.
 *
 * <p>save() uses load-or-insert semantics so that a subsequent save of the same
 * aggregate updates the existing row rather than throwing on duplicate PK. The
 * aggregate's UUID v7 is the PK — Hibernate's default isNew() heuristic with
 * externally-assigned IDs would otherwise MERGE (triggering SELECT+INSERT); this
 * explicit lookup keeps the SQL predictable.</p>
 */
@Repository
class GrantRepositoryImpl implements GrantRepository {

    private final GrantJpaRepository jpa;

    GrantRepositoryImpl(GrantJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Grant grant) {
        GrantJpaEntity entity = jpa.findById(grant.id().value())
            .map(existing -> updateEntity(existing, grant))
            .orElseGet(() -> newEntity(grant));
        jpa.save(entity);
    }

    @Override
    public Optional<Grant> findById(GrantId id) {
        return jpa.findById(id.value()).map(this::toDomain);
    }

    @Override
    public boolean existsActiveByBeneficiaryAndPlan(UUID beneficiaryId, UUID planId) {
        return jpa.existsByBeneficiaryIdAndPlanIdAndStatus(beneficiaryId, planId, "ACTIVE");
    }

    private GrantJpaEntity newEntity(Grant grant) {
        GrantJpaEntity e = new GrantJpaEntity();
        e.setId(grant.id().value());
        return updateEntity(e, grant);
    }

    private GrantJpaEntity updateEntity(GrantJpaEntity e, Grant grant) {
        e.setBeneficiaryId(grant.beneficiaryId());
        e.setPlanId(grant.plan().planId());
        e.setPlanName(grant.plan().planName());
        e.setMerchantCategories(grant.plan().categories());
        e.setStatus(grant.status().name());
        e.setValidFrom(grant.validity().from());
        e.setValidUntil(grant.validity().until());
        e.setCycleYear((short) grant.cycle().year());
        e.setCycleMonth((short) grant.cycle().month());
        e.setAmountValue(grant.amount().amount());
        e.setAmountCurrency(grant.amount().currency().getCurrencyCode());
        return e;
    }

    private Grant toDomain(GrantJpaEntity e) {
        return Grant.rehydrate(
            new GrantId(e.getId()),
            e.getBeneficiaryId(),
            new BenefitPlan(e.getPlanId(), e.getPlanName(), e.getMerchantCategories()),
            Money.of(e.getAmountValue(), Currency.getInstance(e.getAmountCurrency())),
            Period.of(e.getValidFrom(), e.getValidUntil()),
            Cycle.of(e.getCycleYear(), e.getCycleMonth()),
            GrantStatus.valueOf(e.getStatus())
        );
    }
}
