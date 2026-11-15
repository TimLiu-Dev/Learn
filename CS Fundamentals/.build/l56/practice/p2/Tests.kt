import org.junit.Assert.assertEquals
import org.junit.Test

class RecoverTest {
    private fun three() = encode("k1", "v1") + encode("k2", "v2") + encode("k1", "v3")

    @Test fun `crcHex is the CRC-32 as 8 hex digits`() {
        assertEquals("3610a686", crcHex("hello"))
        assertEquals("a CRC that starts with a zero digit still has 8 digits", "019cc60d", crcHex("dune"))
    }

    @Test fun `a clean log recovers every record and a later value wins`() {
        val r = recover(three())
        assertEquals(mapOf("k1" to "v3", "k2" to "v2"), r.data)
        assertEquals(three().length, r.validLength)
    }

    @Test fun `a cut anywhere inside the last record drops only that record`() {
        val start = three().length - encode("k1", "v3").length
        for (cut in start until three().length) {
            val r = recover(three().substring(0, cut))
            assertEquals("cut at $cut of ${three().length}: data", mapOf("k1" to "v1", "k2" to "v2"), r.data)
            assertEquals("cut at $cut of ${three().length}: validLength", start, r.validLength)
        }
    }

    @Test fun `a bad checksum ends the log, and nothing after it is trusted`() {
        val bad = three().replace("k2=v2", "k2=v9")              // the bytes changed but the CRC didn't
        val r = recover(bad)
        assertEquals(mapOf("k1" to "v1"), r.data)
        assertEquals(encode("k1", "v1").length, r.validLength)
    }

    @Test fun `junk after the last record is ignored`() {
        val r = recover(three() + "1f2e3d4c k9=\u0000\u0000\n")
        assertEquals(mapOf("k1" to "v3", "k2" to "v2"), r.data)
        assertEquals(three().length, r.validLength)
    }

    @Test fun `a write appended after truncating survives the next recovery`() {
        val torn = three().dropLast(4)
        val repaired = torn.substring(0, recover(torn).validLength) + encode("k4", "v4")
        assertEquals("recovered from: $repaired", mapOf("k1" to "v1", "k2" to "v2", "k4" to "v4"), recover(repaired).data)
    }
}
