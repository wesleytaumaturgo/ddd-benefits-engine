package com.wtaumaturgo.benefits.grant.application;

import com.wtaumaturgo.benefits.grant.application.dto.RenewGrantCommand;
import com.wtaumaturgo.benefits.grant.domain.exception.GrantNotFoundException;
import com.wtaumaturgo.benefits.grant.domain.model.BenefitPlan;
import com.wtaumaturgo.benefits.grant.domain.model.Grant;
import com.wtaumaturgo.benefits.grant.domain.model.GrantId;
import com.wtaumaturgo.benefits.grant.domain.model.GrantRenewed;
import com.wtaumaturgo.benefits.grant.domain.model.MerchantCategory;
import com.wtaumaturgo.benefits.grant.domain.repository.GrantRepository;
import com.wtaumaturgo.benefits.shared.domain.model.Cycle;
import com.wtaumaturgo.benefits.shared.domain.model.Money;
import com.wtaumaturgo.benefits.shared.domain.model.Period;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RenewGrantUseCaseTest {

    @Mock GrantRepository repository;
    @Mock ApplicationEventPublisher publisher;

    RenewGrantUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RenewGrantUseCase(repository, publisher);
    }

    @Test
    void shouldThrowWhenGrantNotFound() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        RenewGrantCommand cmd = new RenewGrantCommand(
            new GrantId(UUID.randomUUID()),
            Period.of(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 6, 1))
        );

        assertThatThrownBy(() -> useCase.execute(cmd))
            .isInstanceOf(GrantNotFoundException.class);

        verify(repository, never()).save(any());
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void shouldSaveAndPublishGrantRenewed() {
        Grant grant = Grant.create(
            UUID.randomUUID(),
            new BenefitPlan(UUID.randomUUID(), "Plano", Set.of(MerchantCategory.FUEL_STATION)),
            Money.of(new BigDecimal("100.00"), Currency.getInstance("BRL")),
            Period.of(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1)),
            Cycle.of(2026, 4)
        );
        grant.pullDomainEvents();  // drop GrantCreated
        when(repository.findById(grant.id())).thenReturn(Optional.of(grant));

        Period newValidity = Period.of(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 6, 1));
        useCase.execute(new RenewGrantCommand(grant.id(), newValidity));

        verify(repository).save(grant);
        verify(publisher).publishEvent(any(GrantRenewed.class));
    }
}
