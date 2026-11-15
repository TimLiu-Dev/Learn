import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

// The example.com chain that openssl printed in Part 2 (subjects shortened to their CN).
val leaf = Cert("example.com", "Cloudflare TLS Issuing ECC CA 3",
                listOf("example.com", "*.example.com"), Instant.parse("2026-12-25T22:56:35Z"))
val issuing = Cert("Cloudflare TLS Issuing ECC CA 3", "SSL.com TLS Transit ECC CA R2",
                   emptyList(), Instant.parse("2035-05-27T19:49:44Z"))
val transit = Cert("SSL.com TLS Transit ECC CA R2", "SSL.com TLS ECC Root CA 2022",
                   emptyList(), Instant.parse("2037-10-17T17:02:22Z"))
val crossSigned = Cert("SSL.com TLS ECC Root CA 2022", "AAA Certificate Services",
                       emptyList(), Instant.parse("2028-12-31T23:59:59Z"))
val chain = listOf(leaf, issuing, transit, crossSigned)
val now: Instant = Instant.parse("2026-11-14T12:00:00Z")
val jdkRoots = setOf("AAA Certificate Services", "ISRG Root X1")

class ChainTest {
    @Test fun `the real example dot com chain verifies`() {
        assertEquals("OK", verify(chain, "example.com", now, jdkRoots))
        assertEquals("OK", verify(chain, "www.example.com", now, jdkRoots))
    }

    @Test fun `a wildcard covers exactly one label, in any case`() {
        assertEquals("*.example.com vs www.example.com", true, covers("*.example.com", "www.example.com"))
        assertEquals("*.example.com vs example.com", false, covers("*.example.com", "example.com"))
        assertEquals("*.example.com vs a.b.example.com", false, covers("*.example.com", "a.b.example.com"))
        assertEquals("*.Example.com vs WWW.example.COM", true, covers("*.Example.com", "WWW.example.COM"))
        assertEquals("example.com vs notexample.com", false, covers("example.com", "notexample.com"))
    }

    @Test fun `a host the leaf doesn't name is a mismatch`() {
        assertEquals("name mismatch", verify(chain, "example.org", now, jdkRoots))
        assertEquals("name mismatch", verify(chain, "api.v2.example.com", now, jdkRoots))
    }

    @Test fun `an expired certificate is reported by subject`() {
        assertEquals("checked a year after the leaf expired", "expired: example.com",
                     verify(chain, "example.com", Instant.parse("2027-12-25T00:00:00Z"), jdkRoots))
    }

    @Test fun `a missing intermediate breaks the chain`() {
        assertEquals("broken chain at 0", verify(listOf(leaf, transit, crossSigned), "example.com", now, jdkRoots))
    }

    @Test fun `a chain that ends at an unknown root is untrusted`() {
        assertEquals("untrusted root", verify(chain, "example.com", now, setOf("ISRG Root X1")))
    }
}
