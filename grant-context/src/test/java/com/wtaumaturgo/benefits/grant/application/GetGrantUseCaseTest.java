package com.wtaumaturgo.benefits.grant.application;

import com.wtaumaturgo.benefits.grant.domain.model.BenefitPlan;
import com.wtaumaturgo.benefits.grant.domain.model.Grant;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetGrantUseCaseTest {

    @Mock
    GrantRepository repository;

    @InjectMocks
    GetGrantUseCase useCase;

    @Test
    void execute_returnsEmptyOptional_whenGrantNotFound() {
        GrantId id = new GrantId(UUID.randomUUID());
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThat(useCase.execute(id)).isEmpty();
    }

    @Test
    void execute_returnsGrant_whenFound() {
        Grant grant = Grant.create(
            UUID.randomUUID(),
            new BenefitPlan(UUID.randomUUID(), "Plan", Set.of(MerchantCategory.SUPERMARKET)),
            Money.of(new BigDecimal("150.00"), Currency.getInstance("BRL")),
            Period.of(LocalDate.now(), LocalDate.now().plusMonths(1)),
            Cycle.of(2026, 4)
        );
        when(repository.findById(grant.id())).thenReturn(Optional.of(grant));

        Optional<Grant> result = useCase.execute(grant.id());

        assertThat(result).isPresent().contains(grant);
    }
}
