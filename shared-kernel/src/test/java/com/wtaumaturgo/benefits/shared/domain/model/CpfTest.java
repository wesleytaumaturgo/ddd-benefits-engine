package com.wtaumaturgo.benefits.shared.domain.model;

import com.wtaumaturgo.benefits.shared.exception.InvalidCpfException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

class CpfTest {

    // --- Valid CPFs (three known-valid cases for FUND-13; digits verified against the mod-11 algorithm) ---

    @Test
    void shouldAcceptValidCpfWithPunctuation() {
        assertThatCode(() -> new CPF("529.982.247-25")).doesNotThrowAnyException();
    }

    @Test
    void shouldAcceptSecondKnownValidCpf() {
        assertThatCode(() -> new CPF("012.345.678-90")).doesNotThrowAnyException();
    }

    @Test
    void shouldAcceptThirdKnownValidCpf() {
        assertThatCode(() -> new CPF("111.222.333-96")).doesNotThrowAnyException();
    }

    @Test
    void shouldAcceptValidCpfWithoutPunctuation() {
        assertThatCode(() -> new CPF("52998224725")).doesNotThrowAnyException();
    }

    @Test
    void valueShouldReturnDigitsOnly() {
        CPF cpf = new CPF("529.982.247-25");
        assertThat(cpf.value()).isEqualTo("52998224725");
    }

    @Test
    void formattedShouldReturnPunctuatedForm() {
        CPF cpf = new CPF("529.982.247-25");
        assertThat(cpf.formatted()).isEqualTo("529.982.247-25");
    }

    // --- Invalid check digits ---

    @Test
    void shouldRejectCpfWithWrongFirstCheckDigit() {
        assertThatThrownBy(() -> new CPF("529.982.247-35"))
            .isInstanceOf(InvalidCpfException.class);
    }

    @Test
    void shouldRejectCpfWithWrongSecondCheckDigit() {
        assertThatThrownBy(() -> new CPF("529.982.247-26"))
            .isInstanceOf(InvalidCpfException.class);
    }

    // --- Repeating sequences ---

    @Test
    void shouldRejectAllOnes() {
        assertThatThrownBy(() -> new CPF("111.111.111-11"))
            .isInstanceOf(InvalidCpfException.class);
    }

    @Test
    void shouldRejectAllZeros() {
        assertThatThrownBy(() -> new CPF("000.000.000-00"))
            .isInstanceOf(InvalidCpfException.class);
    }

    @Test
    void shouldRejectAllNines() {
        assertThatThrownBy(() -> new CPF("999.999.999-99"))
            .isInstanceOf(InvalidCpfException.class);
    }

    // --- Formatting / length ---

    @Test
    void shouldRejectTooShort() {
        assertThatThrownBy(() -> new CPF("123456789"))
            .isInstanceOf(InvalidCpfException.class);
    }

    @Test
    void shouldRejectTooLong() {
        assertThatThrownBy(() -> new CPF("12345678901234"))
            .isInstanceOf(InvalidCpfException.class);
    }

    @Test
    void shouldRejectEmptyString() {
        assertThatThrownBy(() -> new CPF(""))
            .isInstanceOf(InvalidCpfException.class);
    }

    @Test
    void shouldRejectNull() {
        assertThatThrownBy(() -> new CPF(null))
            .isInstanceOf(NullPointerException.class);
    }
}
