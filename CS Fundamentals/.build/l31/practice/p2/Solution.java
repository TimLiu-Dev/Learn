import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Practice {
    record Cert(String subject, String issuer, List<String> names, Instant notAfter) { }

    // Does one name from a certificate cover the host? "*.example.com" covers exactly one extra label.
    static boolean covers(String name, String host) {
        String n = name.toLowerCase();
        String h = host.toLowerCase();
        if (!n.startsWith("*.")) return n.equals(h);
        int dot = h.indexOf('.');
        return dot >= 0 && h.substring(dot + 1).equals(n.substring(2));   // drop the host's first label
    }

    // "OK", or the first problem found: "name mismatch", "expired: SUBJECT", "broken chain at I", "untrusted root"
    static String verify(List<Cert> chain, String host, Instant now, Set<String> trusted) {
        if (chain.get(0).names().stream().noneMatch(n -> covers(n, host))) return "name mismatch";
        for (Cert c : chain) {
            if (c.notAfter().isBefore(now)) return "expired: " + c.subject();
        }
        for (int i = 0; i + 1 < chain.size(); i++) {
            if (!chain.get(i).issuer().equals(chain.get(i + 1).subject())) return "broken chain at " + i;
        }
        return trusted.contains(chain.get(chain.size() - 1).issuer()) ? "OK" : "untrusted root";
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
