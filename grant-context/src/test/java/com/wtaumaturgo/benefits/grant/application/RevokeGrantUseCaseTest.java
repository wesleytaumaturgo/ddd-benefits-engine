package com.wtaumaturgo.benefits.grant.application;

import com.wtaumaturgo.benefits.grant.application.dto.RevokeGrantCommand;
import com.wtaumaturgo.benefits.grant.domain.exception.GrantNotFoundException;
import com.wtaumaturgo.benefits.grant.domain.model.Grant;
import com.wtaumaturgo.benefits.grant.domain.model.BenefitPlan;
import com.wtaumaturgo.benefits.grant.domain.model.GrantId;
import com.wtaumaturgo.benefits.grant.domain.model.MerchantCategory;
import com.wtaumaturgo.benefits.grant.domain.repository.GrantRepository;
import com.wtaumaturgo.benefits.shared.domain.model.Cycle;
import com.wtaumaturgo.benefits.shared.domain.model.Money;
import com.wtaumaturgo.benefits.shared.domain.model.Period;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RevokeGrantUseCaseTest {

    @Mock
    GrantRepository repository;

    @Mock
    ApplicationEventPublisher events;

    @InjectMocks
    RevokeGrantUseCase useCase;

    private Grant sampleGrant() {
        return Grant.create(
            UUID.randomUUID(),
            new BenefitPlan(UUID.randomUUID(), "Plan", Set.of(MerchantCategory.RESTAURANT)),
            Money.of(new BigDecimal("200.00"), Currency.getInstance("BRL")),
            Period.of(LocalDate.now(), LocalDate.now().plusMonths(1)),
            Cycle.of(2026, 4)
        );
    }

    @Test
    void execute_throwsGrantNotFoundException_whenGrantNotFound() {
        GrantId id = new GrantId(UUID.randomUUID());
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new RevokeGrantCommand(id)))
            .isInstanceOf(GrantNotFoundException.class);

        verify(repository, never()).save(any());
        verify(events, never()).publishEvent(any());
    }

    @Test
    void execute_savesAndPublishesEvent_whenGrantExists() {
        Grant grant = sampleGrant();
        // drain the GrantCreated event from create()
        grant.pullDomainEvents();
        GrantId id = grant.id();
        when(repository.findById(id)).thenReturn(Optional.of(grant));

        var inOrder = inOrder(repository, events);
        useCase.execute(new RevokeGrantCommand(id));

        inOrder.verify(repository).save(grant);
        inOrder.verify(events, atLeastOnce()).publishEvent(any());
    }
}
