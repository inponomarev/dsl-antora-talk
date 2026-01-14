package org.example;

import net.jqwik.api.*;
import org.example.GCD.Lang;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.Parameter;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

@ParameterizedClass
@EnumSource(Lang.class)
class GCDTest {
    @Parameter
    Lang lang;
    // -----------------------
    // JUnit 5 parameterized (CSV)
    // -----------------------

    @ParameterizedTest(name = "gcd({0}, {1}) = {2}")
    @CsvSource({
            "54,   24,   6",
            "24,   54,   6",
            "0,    5,    5",
            "5,    0,    5",
            "0,    0,    0",
            "-54,  24,   6",
            "54,  -24,   6",
            "-54, -24,   6",
            "270,  192,  6",
            "1071, 462,  21"
    })
    @DisplayName("gcd: known examples")
    void gcd_examples(long a, long b, long expected) {
        assertThat(GCD.gcd(a, b, lang)).isEqualTo(expected);
    }

    // -----------------------
    // jqwik properties (kept, but now examples are CSV-based)
    // -----------------------

    @Property(tries = 2_000)
    void isCommutative(@ForAll("safeLongs") long a, @ForAll("safeLongs") long b, @ForAll Lang lang) {
        assertThat(GCD.gcd(a, b, lang)).isEqualTo(GCD.gcd(b, a, lang));
    }

    @Property(tries = 2_000)
    void dividesBothNumbers(@ForAll("safeLongs") long a, @ForAll("safeLongs") long b, @ForAll Lang lang) {
        long g = GCD.gcd(a, b, lang);

        if (g == 0) {
            // Under the usual definition gcd(0,0)=0.
            assertThat(a).isZero();
            assertThat(b).isZero();
            return;
        }

        assertThat(a % g).isZero();
        assertThat(b % g).isZero();
    }

    @Property(tries = 2_000)
    void matchesBigIntegerGcd(@ForAll("safeLongs") long a, @ForAll("safeLongs") long b, @ForAll Lang lang) {
        long expected = BigInteger.valueOf(a).gcd(BigInteger.valueOf(b)).longValueExact();
        assertThat(GCD.gcd(a, b, lang)).isEqualTo(expected);
    }

    // -----------------------
    // Generators
    // -----------------------

    @Provide
    Arbitrary<Long> safeLongs() {
        // keep moderate range; avoids overflow concerns in many gcd impls
        return Arbitraries.longs().between(-100_000_000L, 100_000_000L);
    }

}
