package com.wtaumaturgo.benefits.grant.application;

import com.wtaumaturgo.benefits.grant.application.dto.CreateGrantCommand;
import com.wtaumaturgo.benefits.grant.domain.exception.DuplicateActiveGrantException;
import com.wtaumaturgo.benefits.grant.domain.model.BenefitPlan;
import com.wtaumaturgo.benefits.grant.domain.model.Grant;
import com.wtaumaturgo.benefits.grant.domain.model.GrantId;
import com.wtaumaturgo.benefits.grant.domain.repository.GrantRepository;
import com.wtaumaturgo.benefits.shared.domain.model.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Creates a new Grant aggregate. Enforces GRANT-02 (no duplicate active grant
 * per beneficiary/plan) at the application layer because the check spans
 * multiple aggregates and cannot live inside {@link Grant}.
 *
 * <p>Event publication order (GRANT-07): repository.save() first, then
 * {@link Grant#pullDomainEvents()} drain, then publishEvent() for each event.
 * Publishing inside the transactional method is correct — downstream
 * {@code @TransactionalEventListener(AFTER_COMMIT)} consumers (Phase 3) only
 * fire on commit; plain listeners fire synchronously.</p>
 */
@Service
public class CreateGrantUseCase {

    private final GrantRepository repository;
    private final ApplicationEventPublisher events;

    public CreateGrantUseCase(GrantRepository repository, ApplicationEventPublisher events) {
        this.repository = repository;
        this.events = events;
    }

    @Transactional
    public GrantId execute(CreateGrantCommand cmd) {
        if (repository.existsActiveByBeneficiaryAndPlan(cmd.beneficiaryId(), cmd.planId())) {
            throw new DuplicateActiveGrantException(cmd.beneficiaryId(), cmd.planId());
        }

        Grant grant = Grant.create(
            cmd.beneficiaryId(),
            new BenefitPlan(cmd.planId(), cmd.planName(), cmd.categories()),
            cmd.amount(),
            cmd.validity(),
            cmd.cycle()
        );

        repository.save(grant);

        List<DomainEvent> drained = grant.pullDomainEvents();
        for (DomainEvent event : drained) {
            events.publishEvent(event);
        }

        return grant.id();
    }
}
