package com.wtaumaturgo.benefits.shared.util;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AggregateIdGeneratorTest {

    @Test
    void newIdShouldReturnNonNullUuid() {
        UUID id = AggregateIdGenerator.newId();
        assertThat(id).isNotNull();
    }

    @Test
    void consecutiveIdsShouldBeDifferent() {
        UUID id1 = AggregateIdGenerator.newId();
        UUID id2 = AggregateIdGenerator.newId();
        assertThat(id1).isNotEqualTo(id2);
    }
}
