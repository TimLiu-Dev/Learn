package gateway

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TraceparentTest {
    @Test fun `a generated header has the W3C shape`() {
        val header = traceparent(randomHex(16), randomHex(8))
        assertTrue(Regex("^00-[0-9a-f]{32}-[0-9a-f]{16}-01$").matches(header), header)
    }

    @Test fun `parsing returns trace id and parent id`() {
        assertEquals("4bf92f3577b34da6a3ce929d0e0e4736" to "00f067aa0ba902b7",
            parseTraceparent("00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01"))
    }

    @Test fun `all-zero ids and junk are rejected`() {
        assertNull(parseTraceparent("00-00000000000000000000000000000000-00f067aa0ba902b7-01"))
        assertNull(parseTraceparent("hello"))
        assertNull(parseTraceparent(null))
    }
}
