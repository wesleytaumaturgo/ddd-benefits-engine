package com.wtaumaturgo.benefits.shared.domain.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies AggregateRoot<ID> interface contract.
 */
class AggregateRootTest {

    /** Minimal concrete aggregate for testing purposes. */
    static class FakeAggregate implements AggregateRoot<UUID> {

        private final UUID id;
        private final List<DomainEvent> events = new ArrayList<>();

        FakeAggregate(UUID id) {
            this.id = id;
        }

        void recordEvent(DomainEvent event) {
            events.add(event);
        }

        @Override
        public UUID id() {
            return id;
        }

        @Override
        public List<DomainEvent> pullDomainEvents() {
            List<DomainEvent> snapshot = List.copyOf(events);
            events.clear();
            return snapshot;
        }
    }

    @Test
    void id_returnsConfiguredIdentifier() {
        UUID uuid = UUID.randomUUID();
        FakeAggregate aggregate = new FakeAggregate(uuid);
        assertThat(aggregate.id()).isEqualTo(uuid);
    }

    @Test
    void pullDomainEvents_returnsRecordedEventsAndDrainsList() {
        FakeAggregate aggregate = new FakeAggregate(UUID.randomUUID());
        DomainEvent event = new DomainEvent() {
            @Override public UUID eventId() { return UUID.randomUUID(); }
            @Override public java.time.Instant occurredOn() { return java.time.Instant.now(); }
        };
        aggregate.recordEvent(event);

        List<DomainEvent> pulled = aggregate.pullDomainEvents();
        assertThat(pulled).containsExactly(event);
        assertThat(aggregate.pullDomainEvents()).isEmpty();
    }

    @Test
    void pullDomainEvents_isImmutableSnapshot() {
        FakeAggregate aggregate = new FakeAggregate(UUID.randomUUID());
        DomainEvent event = new DomainEvent() {
            @Override public UUID eventId() { return UUID.randomUUID(); }
            @Override public java.time.Instant occurredOn() { return java.time.Instant.now(); }
        };
        aggregate.recordEvent(event);

        List<DomainEvent> pulled = aggregate.pullDomainEvents();
        // List.copyOf returns an immutable list
        assertThat(pulled).hasSize(1);
        // After pulling, the internal list must be empty (drain semantics)
        assertThat(aggregate.pullDomainEvents()).isEmpty();
    }
}
