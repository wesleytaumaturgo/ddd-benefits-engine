package com.wtaumaturgo.benefits.shared.domain.model;

import com.wtaumaturgo.benefits.shared.exception.InvalidPeriodException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PeriodTest {

    private static final LocalDate APR_01 = LocalDate.of(2026, 4, 1);
    private static final LocalDate MAY_01 = LocalDate.of(2026, 5, 1);
    private static final LocalDate JUN_01 = LocalDate.of(2026, 6, 1);

    // --- Construction ---

    @Test
    void shouldConstructValidPeriod() {
        assertThatCode(() -> Period.of(APR_01, MAY_01)).doesNotThrowAnyException();
    }

    @Test
    void shouldRejectZeroDuration() {
        assertThatThrownBy(() -> Period.of(APR_01, APR_01))
            .isInstanceOf(InvalidPeriodException.class);
    }

    @Test
    void shouldRejectFromAfterUntil() {
        assertThatThrownBy(() -> Period.of(MAY_01, APR_01))
            .isInstanceOf(InvalidPeriodException.class);
    }

    @Test
    void shouldRejectNullFrom() {
        assertThatThrownBy(() -> Period.of(null, MAY_01))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRejectNullUntil() {
        assertThatThrownBy(() -> Period.of(APR_01, null))
            .isInstanceOf(NullPointerException.class);
    }

    // --- contains() ---

    @Test
    void containsShouldIncludeFromDate() {
        Period period = Period.of(APR_01, MAY_01);
        assertThat(period.contains(APR_01)).isTrue();
    }

    @Test
    void containsShouldIncludeDayBeforeUntil() {
        Period period = Period.of(APR_01, MAY_01);
        assertThat(period.contains(LocalDate.of(2026, 4, 30))).isTrue();
    }

    @Test
    void containsShouldExcludeUntilDate() {
        Period period = Period.of(APR_01, MAY_01);
        assertThat(period.contains(MAY_01)).isFalse();
    }

    @Test
    void containsShouldExcludeDateBeforeFrom() {
        Period period = Period.of(APR_01, MAY_01);
        assertThat(period.contains(LocalDate.of(2026, 3, 31))).isFalse();
    }

    // --- overlaps() ---

    @Test
    void shouldOverlapWithGenuinelyOverlappingPeriod() {
        Period april = Period.of(APR_01, MAY_01);
        Period mid = Period.of(LocalDate.of(2026, 4, 15), JUN_01);
        assertThat(april.overlaps(mid)).isTrue();
    }

    @Test
    void adjacentPeriodsMustNotOverlap() {
        // [APR_01, MAY_01) and [MAY_01, JUN_01) are adjacent — critical case per D-04
        Period april = Period.of(APR_01, MAY_01);
        Period may = Period.of(MAY_01, JUN_01);
        assertThat(april.overlaps(may)).isFalse();
    }

    @Test
    void shouldNotOverlapWithCompletelyDisjointPeriod() {
        Period april = Period.of(APR_01, MAY_01);
        Period june = Period.of(JUN_01, LocalDate.of(2026, 7, 1));
        assertThat(april.overlaps(june)).isFalse();
    }

    @Test
    void overlapShouldBeSymmetric() {
        Period april = Period.of(APR_01, MAY_01);
        Period may = Period.of(MAY_01, JUN_01);
        assertThat(april.overlaps(may)).isEqualTo(may.overlaps(april));
    }

    // --- durationInDays() ---

    @Test
    void aprilDurationIs30Days() {
        Period april = Period.of(APR_01, MAY_01);
        assertThat(april.durationInDays()).isEqualTo(30L);
    }

    @Test
    void januaryDurationIs31Days() {
        Period january = Period.of(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1));
        assertThat(january.durationInDays()).isEqualTo(31L);
    }

    // --- toString() ---

    @Test
    void toStringShouldShowSemiOpenNotation() {
        Period period = Period.of(APR_01, MAY_01);
        assertThat(period.toString()).isEqualTo("[2026-04-01, 2026-05-01)");
    }

    // --- equals / hashCode ---

    @Test
    void periodsWithSameFromAndUntilShouldBeEqual() {
        Period p1 = Period.of(APR_01, MAY_01);
        Period p2 = Period.of(APR_01, MAY_01);
        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
    }

    // --- isInCycle() ---

    @Test
    void shouldBeInCycleWhenOverlappingMonthPeriod() {
        Period p = Period.of(LocalDate.of(2026, 4, 15), LocalDate.of(2026, 5, 15));
        Cycle aprilCycle = Cycle.of(2026, 4);
        assertThat(p.isInCycle(aprilCycle)).isTrue();
    }
}
