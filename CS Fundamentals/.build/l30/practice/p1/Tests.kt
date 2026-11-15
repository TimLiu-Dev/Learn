import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class Ipv4Test {
    private fun hex(n: Int) = Integer.toHexString(n)   // easier to read than a negative Int

    @Test fun `an address becomes one 32-bit number`() {
        assertEquals("c0a80197", hex(parseIpv4("192.168.1.151")))
        assertEquals("7f000001", hex(parseIpv4("127.0.0.1")))
    }

    @Test fun `bad addresses are rejected`() {
        for (bad in listOf("256.1.1.1", "1.2.3", "1.2.3.4.5", "a.b.c.d")) {
            try {
                parseIpv4(bad)
                fail("\"$bad\" should throw IllegalArgumentException")
            } catch (expected: IllegalArgumentException) {
            }
        }
    }

    @Test fun `masks match ifconfig's netmasks`() {
        assertEquals("/24, like en0", "ffffff00", hex(maskOf(24)))
        assertEquals("/8, like lo0", "ff000000", hex(maskOf(8)))
        assertEquals("/32 keeps every bit", "ffffffff", hex(maskOf(32)))
        assertEquals("/0 keeps no bits", "0", hex(maskOf(0)))
    }

    @Test fun `a slash-24 block holds 256 addresses`() {
        assertEquals("192.168.1.77", true, cidrContains("192.168.1.0/24", "192.168.1.77"))
        assertEquals("192.168.1.255", true, cidrContains("192.168.1.0/24", "192.168.1.255"))
        assertEquals("192.168.2.1", false, cidrContains("192.168.1.0/24", "192.168.2.1"))
    }

    @Test fun `RFC 1918's 172 block is a slash-12`() {
        assertEquals("172.31.255.255", true, cidrContains("172.16.0.0/12", "172.31.255.255"))
        assertEquals("172.32.0.1", false, cidrContains("172.16.0.0/12", "172.32.0.1"))
        assertEquals("172.15.0.1", false, cidrContains("172.16.0.0/12", "172.15.0.1"))
    }

    @Test fun `the slash-0 block contains every address`() {
        assertEquals("8.8.8.8", true, cidrContains("0.0.0.0/0", "8.8.8.8"))
        assertEquals("255.255.255.255", true, cidrContains("0.0.0.0/0", "255.255.255.255"))
    }
}
