package katas

import java.math.BigInteger
import kotlin.math.floor
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ProofsTest {
    @Test fun `isqrt of 0 and 1`() {
        assertEquals(0, isqrt(0))
        assertEquals(1, isqrt(1))
    }

    @Test fun `isqrt of every perfect square up to 1000 squared`() {
        for (k in 0..1000) assertEquals(k, isqrt(k * k))
    }

    @Test fun `isqrt matches floor of sqrt for 0 to 1000000`() {
        for (n in 0..1_000_000) assertEquals(floor(sqrt(n.toDouble())).toInt(), isqrt(n))
    }

    @Test fun `isqrt of Int MAX_VALUE`() = assertEquals(46_340, isqrt(Int.MAX_VALUE))

    @Test fun `isqrt rejects negatives`() {
        assertFailsWith<IllegalArgumentException> { isqrt(-1) }
    }

    @Test fun `powMod small known values`() {
        assertEquals(24, powMod(2, 10, 1000))
        assertEquals(1, powMod(7, 0, 13))
        assertEquals(0, powMod(7, 0, 1))
        assertEquals(powMod(4, 3, 7), powMod(-3, 3, 7))   // -3 and 4 are the same mod 7
    }

    @Test fun `powMod matches BigInteger on 1000 random cases`() {
        val rnd = Random(48)
        repeat(1000) {
            val base = rnd.nextLong(-1_000_000_000_000, 1_000_000_000_000)
            val exp = rnd.nextLong(0, Long.MAX_VALUE)
            val m = rnd.nextLong(1, 3_037_000_501)
            val expected = BigInteger.valueOf(base).modPow(BigInteger.valueOf(exp), BigInteger.valueOf(m)).toLong()
            assertEquals(expected, powMod(base, exp, m), "base=$base exp=$exp m=$m")
        }
    }
}
