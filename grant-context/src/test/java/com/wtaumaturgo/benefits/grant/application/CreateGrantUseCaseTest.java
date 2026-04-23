package com.wtaumaturgo.benefits.grant.application;

import com.wtaumaturgo.benefits.grant.application.dto.CreateGrantCommand;
import com.wtaumaturgo.benefits.grant.domain.exception.DuplicateActiveGrantException;
import com.wtaumaturgo.benefits.grant.domain.model.Grant;
import com.wtaumaturgo.benefits.grant.domain.model.GrantCreated;
import com.wtaumaturgo.benefits.grant.domain.model.MerchantCategory;
import com.wtaumaturgo.benefits.grant.domain.repository.GrantRepository;
import com.wtaumaturgo.benefits.shared.domain.model.Cycle;
import com.wtaumaturgo.benefits.shared.domain.model.Money;
import com.wtaumaturgo.benefits.shared.domain.model.Period;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateGrantUseCaseTest {

    @Mock GrantRepository repository;
    @Mock ApplicationEventPublisher publisher;

    CreateGrantUseCase useCase;

    CreateGrantCommand validCommand;

    @BeforeEach
    void setUp() {
        useCase = new CreateGrantUseCase(repository, publisher);
        validCommand = new CreateGrantCommand(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Plano",
            Set.of(MerchantCategory.FUEL_STATION),
            Money.of(new BigDecimal("100.00"), Currency.getInstance("BRL")),
            Period.of(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1)),
            Cycle.of(2026, 4)
        );
    }

    @Test
    void duplicateShouldThrow() {
        when(repository.existsActiveByBeneficiaryAndPlan(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(validCommand))
            .isInstanceOf(DuplicateActiveGrantException.class);

        verify(repository, never()).save(any());
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void publishesAfterSave() {
        when(repository.existsActiveByBeneficiaryAndPlan(any(), any())).thenReturn(false);

        useCase.execute(validCommand);

        InOrder inOrder = Mockito.inOrder(repository, publisher);
        inOrder.verify(repository).save(any(Grant.class));
        inOrder.verify(publisher).publishEvent(any(GrantCreated.class));
    }

    @Test
    void shouldPublishOneEventPerDrain() {
        when(repository.existsActiveByBeneficiaryAndPlan(any(), any())).thenReturn(false);

        useCase.execute(validCommand);

        verify(publisher).publishEvent(any(GrantCreated.class));
    }
}
