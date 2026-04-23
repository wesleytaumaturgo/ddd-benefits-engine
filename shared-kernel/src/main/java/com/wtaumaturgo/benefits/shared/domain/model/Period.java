package com.wtaumaturgo.benefits.shared.domain.model;

import com.wtaumaturgo.benefits.shared.exception.InvalidPeriodException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Semi-open date interval {@code [from, until)} — {@code from} is inclusive,
 * {@code until} is exclusive. Zero-duration ranges are rejected at construction.
 *
 * <p>Adjacency does not mean overlap: {@code [A, B)} and {@code [B, C)} are
 * contiguous without overlap, which is the behavior required for contiguous
 * renewal scenarios in the domain.</p>
 */
public final class Period {

    private final LocalDate from;
    private final LocalDate until;

    private Period(LocalDate from, LocalDate until) {
        Objects.requireNonNull(from, "from must not be null");
        Objects.requireNonNull(until, "until must not be null");
        if (!from.isBefore(until)) {
            throw new InvalidPeriodException(
                "Period from must be strictly before until: [" + from + ", " + until + ")");
        }
        this.from = from;
        this.until = until;
    }

    public static Period of(LocalDate from, LocalDate until) {
        return new Period(from, until);
    }

    public LocalDate from() {
        return from;
    }

    public LocalDate until() {
        return until;
    }

    /** {@code from} inclusive, {@code until} exclusive. */
    public boolean contains(LocalDate date) {
        Objects.requireNonNull(date, "date must not be null");
        return !date.isBefore(from) && date.isBefore(until);
    }

    /**
     * Two periods overlap when at least one day belongs to both.
     * Adjacent periods {@code [A, B)} and {@code [B, C)} return {@code false}.
     */
    public boolean overlaps(Period other) {
        Objects.requireNonNull(other, "other must not be null");
        return this.from.isBefore(other.until) && other.from.isBefore(this.until);
    }

    public long durationInDays() {
        return ChronoUnit.DAYS.between(from, until);
    }

    public boolean isInCycle(Cycle cycle) {
        Objects.requireNonNull(cycle, "cycle must not be null");
        return overlaps(cycle.toPeriod());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Period other)) return false;
        return from.equals(other.from) && until.equals(other.until);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, until);
    }

    @Override
    public String toString() {
        return "[" + from + ", " + until + ")";
    }
}
