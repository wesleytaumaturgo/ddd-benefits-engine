package com.wtaumaturgo.benefits.grant.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GrantIdTest {

    @Test
    void shouldConstructValidGrantId() {
        assertThatCode(() -> new GrantId(UUID.randomUUID())).doesNotThrowAnyException();
    }

    @Test
    void shouldRejectNullUuid() {
        assertThatThrownBy(() -> new GrantId(null))
            .isInstanceOf(NullPointerException.class);
    }
}
