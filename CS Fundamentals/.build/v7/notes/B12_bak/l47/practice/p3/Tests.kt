import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class SecurityTxtTest {
    private val now = Instant.parse("2026-11-21T12:00:00Z")

    @Test fun `a valid file has no problems`() {
        val txt = """
            # Our security contacts
            Contact: mailto:security@example.com
            Contact: https://example.com/security

            Policy: https://example.com/disclosure-policy
            Expires: 2027-06-30T23:59:59Z
        """.trimIndent()
        assertEquals(emptyList<String>(), problems(txt, now))
    }

    @Test fun `field names are case-insensitive`() {
        assertEquals(emptyList<String>(), problems("contact: tel:+1-201-555-0123\nEXPIRES: 2027-01-01T00:00:00Z", now))
    }

    @Test fun `both required fields are reported missing`() {
        assertEquals(listOf("missing Contact", "missing Expires"), problems("# nothing here\nPolicy: https://example.com/p", now))
    }

    @Test fun `a plain http contact is rejected`() {
        assertEquals(listOf("Contact must use mailto:, tel: or https://: http://example.com/security"),
            problems("Contact: http://example.com/security\nExpires: 2027-01-01T00:00:00Z", now))
    }

    @Test fun `a stale file says when it expired`() {
        assertEquals(listOf("expired on 2025-12-31T18:37:07Z"),
            problems("Contact: mailto:security@example.com\nExpires: 2025-12-31T18:37:07Z", now))
    }

    @Test fun `Expires may appear only once`() {
        assertEquals(listOf("Expires must appear once, found 2"),
            problems("Contact: mailto:a@example.com\nExpires: 2027-01-01T00:00:00Z\nExpires: 2028-01-01T00:00:00Z", now))
    }
}
