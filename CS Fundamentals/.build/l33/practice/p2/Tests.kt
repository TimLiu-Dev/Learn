import org.junit.Assert.*
import org.junit.Test

class VerifyTest {
    private val salt = "sixteen-byte-slt".toByteArray()

    @Test fun `the right password verifies`() {
        assertTrue(verify("correct horse", store("correct horse", salt, 1000)))
    }

    @Test fun `a wrong password does not`() {
        assertFalse(verify("correct horsf", store("correct horse", salt, 1000)))
    }

    @Test fun `a hash stored at another cost still verifies`() {
        assertTrue("read the iteration count from the stored string",
            verify("old password", store("old password", salt, 3000)))
    }

    @Test fun `an unknown algorithm is rejected`() {
        val md5 = store("correct horse", salt, 1000).replaceFirst("pbkdf2-sha256", "md5")
        assertFalse(verify("correct horse", md5))
    }

    @Test fun `malformed strings give false, not an exception`() {
        assertFalse(verify("x", "not-a-hash"))
        assertFalse(verify("x", "pbkdf2-sha256\$lots\$c2FsdA==\$aGFzaA=="))
        assertFalse(verify("x", "pbkdf2-sha256\$1000\$not base64!\$aGFzaA=="))
        assertFalse(verify("x", "pbkdf2-sha256\$0\$c2FsdA==\$aGFzaA=="))
    }
}
