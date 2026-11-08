package katas;

import java.math.BigInteger;
import java.util.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProofsTest {
    @Test void isqrtOfZeroAndOne() {
        assertEquals(0, Proofs.isqrt(0));
        assertEquals(1, Proofs.isqrt(1));
    }

    @Test void isqrtOfEveryPerfectSquareUpTo1000Squared() {
        for (int k = 0; k <= 1000; k++) assertEquals(k, Proofs.isqrt(k * k));
    }

    @Test void isqrtMatchesFloorOfSqrtFor0To1000000() {
        for (int n = 0; n <= 1_000_000; n++) assertEquals((int) Math.floor(Math.sqrt(n)), Proofs.isqrt(n));
    }

    @Test void isqrtOfIntegerMaxValue() { assertEquals(46_340, Proofs.isqrt(Integer.MAX_VALUE)); }

    @Test void isqrtRejectsNegatives() {
        assertThrows(IllegalArgumentException.class, () -> Proofs.isqrt(-1));
    }

    @Test void powModSmallKnownValues() {
        assertEquals(24, Proofs.powMod(2, 10, 1000));
        assertEquals(1, Proofs.powMod(7, 0, 13));
        assertEquals(0, Proofs.powMod(7, 0, 1));
        assertEquals(Proofs.powMod(4, 3, 7), Proofs.powMod(-3, 3, 7));   // -3 and 4 are the same mod 7
    }

    @Test void powModMatchesBigIntegerOn1000RandomCases() {
        Random rnd = new Random(48);
        for (int i = 0; i < 1000; i++) {
            long base = rnd.nextLong(-1_000_000_000_000L, 1_000_000_000_000L);
            long exp = rnd.nextLong(0, Long.MAX_VALUE);
            long m = rnd.nextLong(1, 3_037_000_501L);
            long expected = BigInteger.valueOf(base).modPow(BigInteger.valueOf(exp), BigInteger.valueOf(m)).longValue();
            assertEquals(expected, Proofs.powMod(base, exp, m), "base=" + base + " exp=" + exp + " m=" + m);
        }
    }
}
