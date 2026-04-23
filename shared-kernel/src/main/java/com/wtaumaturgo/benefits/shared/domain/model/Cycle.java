package com.wtaumaturgo.benefits.shared.domain.model;

import java.time.YearMonth;
import java.util.Objects;

/**
 * Monthly cycle identified by year + month. Encapsulates {@link YearMonth}
 * so callers never have to import {@code java.time.YearMonth} to pass a cycle
 * around the domain.
 *
 * <p>{@link #toPeriod()} returns the semi-open {@link Period} covering exactly
 * that month — inclusive on day 1 of the month, exclusive on day 1 of the next
 * month.</p>
 */
public final class Cycle {

    private final YearMonth yearMonth;

    private Cycle(YearMonth yearMonth) {
        this.yearMonth = Objects.requireNonNull(yearMonth, "yearMonth must not be null");
    }

    public static Cycle of(int year, int month) {
        return new Cycle(YearMonth.of(year, month));
    }

    public int year() {
        return yearMonth.getYear();
    }

    public int month() {
        return yearMonth.getMonthValue();
    }

    public Cycle next() {
        return new Cycle(yearMonth.plusMonths(1));
    }

    public Cycle previous() {
        return new Cycle(yearMonth.minusMonths(1));
    }

    public Period toPeriod() {
        return Period.of(yearMonth.atDay(1), yearMonth.plusMonths(1).atDay(1));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cycle other)) return false;
        return yearMonth.equals(other.yearMonth);
    }

    @Override
    public int hashCode() {
        return yearMonth.hashCode();
    }

    @Override
    public String toString() {
        return yearMonth.toString();
    }
}
