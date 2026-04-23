package com.wtaumaturgo.benefits.grant.application;

import com.wtaumaturgo.benefits.grant.application.dto.RevokeGrantCommand;
import com.wtaumaturgo.benefits.grant.domain.exception.GrantNotFoundException;
import com.wtaumaturgo.benefits.grant.domain.model.Grant;
import com.wtaumaturgo.benefits.grant.domain.repository.GrantRepository;
import com.wtaumaturgo.benefits.shared.domain.model.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Revokes a Grant (GRANT-04). Emits GrantRevoked after commit. */
@Service
public class RevokeGrantUseCase {

    private final GrantRepository repository;
    private final ApplicationEventPublisher events;

    public RevokeGrantUseCase(GrantRepository repository, ApplicationEventPublisher events) {
        this.repository = repository;
        this.events = events;
    }

    @Transactional
    public void execute(RevokeGrantCommand cmd) {
        Grant grant = repository.findById(cmd.grantId())
            .orElseThrow(() -> new GrantNotFoundException(cmd.grantId()));

        grant.revoke();
        repository.save(grant);

        List<DomainEvent> drained = grant.pullDomainEvents();
        for (DomainEvent event : drained) {
            events.publishEvent(event);
        }
    }
}
