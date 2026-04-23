package com.wtaumaturgo.benefits.grant.application;

import com.wtaumaturgo.benefits.grant.application.dto.CreateGrantCommand;
import com.wtaumaturgo.benefits.grant.domain.exception.DuplicateActiveGrantException;
import com.wtaumaturgo.benefits.grant.domain.model.GrantId;
import com.wtaumaturgo.benefits.grant.domain.model.MerchantCategory;
import com.wtaumaturgo.benefits.grant.domain.repository.GrantRepository;
import com.wtaumaturgo.benefits.shared.domain.model.Cycle;
import com.wtaumaturgo.benefits.shared.domain.model.Money;
import com.wtaumaturgo.benefits.shared.domain.model.Period;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pure unit test for CreateGrantUseCase. No Spring context — Mockito only.
 * Tests verify: GRANT-02 duplicate check, Grant construction, save order, event drain+publish.
 */
@ExtendWith(MockitoExtension.class)
class CreateGrantUseCaseTest {

    @Mock
    GrantRepository repository;

    @Mock
    ApplicationEventPublisher events;

    @InjectMocks
    CreateGrantUseCase useCase;

    private CreateGrantCommand validCommand() {
        return new CreateGrantCommand(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Vale Combustivel",
            Set.of(MerchantCategory.FUEL_STATION),
            Money.of(new BigDecimal("500.00"), Currency.getInstance("BRL")),
            Period.of(LocalDate.now(), LocalDate.now().plusMonths(1)),
            Cycle.of(2026, 4)
        );
    }

    @Test
    void execute_returnsGrantId_whenNoDuplicateExists() {
        CreateGrantCommand cmd = validCommand();
        when(repository.existsActiveByBeneficiaryAndPlan(cmd.beneficiaryId(), cmd.planId()))
            .thenReturn(false);

        GrantId result = useCase.execute(cmd);

        assertThat(result).isNotNull();
        assertThat(result.value()).isNotNull();
    }

    @Test
    void execute_throwsDuplicateActiveGrantException_whenActiveGrantAlreadyExists() {
        CreateGrantCommand cmd = validCommand();
        when(repository.existsActiveByBeneficiaryAndPlan(cmd.beneficiaryId(), cmd.planId()))
            .thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(cmd))
            .isInstanceOf(DuplicateActiveGrantException.class);

        verify(repository, never()).save(any());
        verify(events, never()).publishEvent(any());
    }

    @Test
    void execute_callsSaveBeforePublishingEvents() {
        CreateGrantCommand cmd = validCommand();
        when(repository.existsActiveByBeneficiaryAndPlan(any(), any())).thenReturn(false);

        var inOrder = inOrder(repository, events);

        useCase.execute(cmd);

        inOrder.verify(repository).save(any());
        inOrder.verify(events, atLeastOnce()).publishEvent(any());
    }

    @Test
    void execute_publishesAtLeastOneEvent_afterSave() {
        CreateGrantCommand cmd = validCommand();
        when(repository.existsActiveByBeneficiaryAndPlan(any(), any())).thenReturn(false);

        useCase.execute(cmd);

        verify(events, atLeastOnce()).publishEvent(any());
    }
}
