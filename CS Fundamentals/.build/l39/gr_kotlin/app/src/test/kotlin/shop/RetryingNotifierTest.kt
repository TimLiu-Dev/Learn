package shop

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

// A fake (L38): fails a set number of times, records every attempt, sends nothing
class FlakyFake(private var failures: Int) : Notifier {
    var attempts = 0
    val delivered = mutableListOf<String>()
    override fun send(to: String, message: String) {
        attempts++
        if (failures-- > 0) throw IllegalStateException("down")
        delivered += "$to:$message"
    }
}

class RetryingNotifierTest {
    @Test fun `succeeds after two failures with three attempts`() {
        val fake = FlakyFake(failures = 2)
        RetryingNotifier(fake, attempts = 3).send("ada", "hi")
        assertEquals(3, fake.attempts)
        assertEquals(listOf("ada:hi"), fake.delivered)
    }

    @Test fun `gives up and rethrows after the last attempt`() {
        val fake = FlakyFake(failures = 5)
        assertFailsWith<IllegalStateException> { RetryingNotifier(fake, attempts = 3).send("ada", "hi") }
        assertEquals(3, fake.attempts)
    }

    @Test fun `factory picks the notifier from config`() {
        assertEquals(EmailNotifier::class, notifierFrom("email:x@test")::class)
        assertFailsWith<IllegalArgumentException> { notifierFrom("pigeon") }
    }
}
