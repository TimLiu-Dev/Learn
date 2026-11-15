import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Practice {
    record Cert(String subject, String issuer, List<String> names, Instant notAfter) { }

    // Does one name from a certificate cover the host? "*.example.com" covers exactly one extra label.
    static boolean covers(String name, String host) {
        throw new UnsupportedOperationException("TODO: compare without case; a leading \"*.\" stands for one label");
    }

    // "OK", or the first problem found, checked in this order:
    //   "name mismatch"       no name in the leaf (chain.get(0)) covers the host
    //   "expired: SUBJECT"    the first certificate whose notAfter is before now
    //   "broken chain at I"   certificate I's issuer isn't certificate I+1's subject
    //   "untrusted root"      the last certificate's issuer isn't in the trust store
    static String verify(List<Cert> chain, String host, Instant now, Set<String> trusted) {
        throw new UnsupportedOperationException("TODO: four checks, in order");
    }

    // CHECKS
    public static void main(String[] args) {
        Cert leaf = new Cert("example.com", "Cloudflare TLS Issuing ECC CA 3",
                List.of("example.com", "*.example.com"), Instant.parse("2026-12-25T22:56:35Z"));
        Cert issuing = new Cert("Cloudflare TLS Issuing ECC CA 3", "SSL.com TLS Transit ECC CA R2",
                List.of(), Instant.parse("2035-05-27T19:49:44Z"));
        Cert transit = new Cert("SSL.com TLS Transit ECC CA R2", "SSL.com TLS ECC Root CA 2022",
                List.of(), Instant.parse("2037-10-17T17:02:22Z"));
        Cert crossSigned = new Cert("SSL.com TLS ECC Root CA 2022", "AAA Certificate Services",
                List.of(), Instant.parse("2028-12-31T23:59:59Z"));
        List<Cert> chain = List.of(leaf, issuing, transit, crossSigned);
        Instant now = Instant.parse("2026-11-14T12:00:00Z");
        Set<String> jdkRoots = Set.of("AAA Certificate Services", "ISRG Root X1");

        check("the real chain verifies", "OK", () -> verify(chain, "example.com", now, jdkRoots));
        check("the wildcard covers www", "OK", () -> verify(chain, "www.example.com", now, jdkRoots));
        check("*.example.com vs example.com", false, () -> covers("*.example.com", "example.com"));
        check("*.example.com vs a.b.example.com", false, () -> covers("*.example.com", "a.b.example.com"));
        check("names compare without case", true, () -> covers("*.Example.com", "WWW.example.COM"));
        check("example.com vs notexample.com", false, () -> covers("example.com", "notexample.com"));
        check("another host is a mismatch", "name mismatch", () -> verify(chain, "example.org", now, jdkRoots));
        check("two labels deep is a mismatch", "name mismatch", () -> verify(chain, "api.v2.example.com", now, jdkRoots));
        check("an expired leaf", "expired: example.com", () -> verify(chain, "example.com", Instant.parse("2027-12-25T00:00:00Z"), jdkRoots));
        check("a missing intermediate", "broken chain at 0", () -> verify(List.of(leaf, transit, crossSigned), "example.com", now, jdkRoots));
        check("an unknown root", "untrusted root", () -> verify(chain, "example.com", now, Set.of("ISRG Root X1")));
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
