import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BatteryTest {
    @Test fun `level is clamped on every assignment`() {
        val b = Battery(100, 50)
        b.level = 150
        assertEquals(100, b.level)
        b.level = -5
        assertEquals(0, b.level)
    }

    @Test fun `the starting level is clamped too`() {
        assertEquals("Battery(100, 250)", 100, Battery(100, 250).level)
        assertEquals("Battery(100, -1)", 0, Battery(100, -1).level)
    }

    @Test fun `percent is computed on every read`() {
        val b = Battery(200, 50)
        assertEquals(25, b.percent)
        b.level = 200
        assertEquals(100, b.percent)
    }

    @Test fun `use drains but never goes below zero`() {
        val b = Battery(100, 50)
        b.use(30)
        assertEquals(20, b.level)
        b.use(30)
        assertEquals(0, b.level)
    }

    @Test fun `recharges can be read but not assigned from outside`() {
        val b = Battery(100, 10)
        b.recharge()
        b.recharge()
        assertEquals(2, b.recharges)
        assertEquals(100, b.level)
        val setters = Battery::class.java.methods.filter { it.name == "setRecharges" }
        assertTrue("recharges still has a public setter: add private set", setters.isEmpty())
    }
}
