import org.junit.Assert.assertEquals
import org.junit.Test

class RedactTest {
    @Test fun `an email is masked`() {
        assertEquals("login ok for [email]", redact("login ok for ana.lima@example.com"))
    }

    @Test fun `punctuation after an email survives`() {
        assertEquals("contact [email], then retry.", redact("contact ana@x.com, then retry."))
    }

    @Test fun `two emails on one line are masked separately`() {
        assertEquals("from [email] to [email]", redact("from a@x.io to b.c+tag@mail.example.org"))
    }

    @Test fun `a card number keeps only its last four digits`() {
        assertEquals("paid [card ****1111] ok", redact("paid 4111 1111 1111 1111 ok"))
        assertEquals("[card ****1881]", redact("4012-8888-8888-1881"))
        assertEquals("card=[card ****0004]", redact("card=5500000000000004"))
    }

    @Test fun `ids, dates, versions and a stray @ are left alone`() {
        val line = "order 12345678 shipped 2026-11-14T09:30Z by build 2.4.21 (see @Test)"
        assertEquals(line, redact(line))
    }

    @Test fun `a 24-digit trace id is not a card`() {
        val line = "trace 123456789012345678901234 done"
        assertEquals(line, redact(line))
    }
}
