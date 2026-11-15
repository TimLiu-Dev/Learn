import org.junit.Assert.assertEquals
import org.junit.Test

class Sample {
    var calls = 0

    @Check fun adds() {
        calls++
        check(1 + 1 == 2)
    }

    @Check fun counts() {
        calls++
        check(calls == 1) { "shared instance: calls=$calls" }
    }

    @Check fun divides() {
        val zero = 0
        println(10 / zero)
    }

    fun helper() {
        throw AssertionError("helper is not a check and must not run")
    }
}

class NoChecks {
    fun plain() {}
}

class RunnerTest {
    @Test fun `only @Check methods run, in name order`() {
        assertEquals(listOf("adds", "counts", "divides"), runChecks(Sample::class.java).map { it.split(" ", ":")[1] })
    }

    @Test fun `a passing check reports PASS`() {
        assertEquals("PASS adds", runChecks(Sample::class.java)[0])
    }

    @Test fun `each check gets a fresh instance`() {
        assertEquals("PASS counts", runChecks(Sample::class.java)[1])
    }

    @Test fun `a failing check reports the real exception`() {
        assertEquals("FAIL divides: ArithmeticException: / by zero", runChecks(Sample::class.java)[2])
    }

    @Test fun `a class without checks gives an empty report`() {
        assertEquals(emptyList<String>(), runChecks(NoChecks::class.java))
    }
}
