import java.time.Instant;
import java.util.*;

public class Practice {
    /** What's wrong with a security.txt (RFC 9116) at now; empty means it's usable. */
    static List<String> problems(String text, Instant now) {
        List<String> contacts = new ArrayList<>();
        List<String> expires = new ArrayList<>();
        for (String raw : text.split("\n")) {
            String line = raw.trim();
            int colon = line.indexOf(':');   // the FIRST colon: values contain colons too
            if (line.isEmpty() || line.startsWith("#") || colon < 0) continue;
            String name = line.substring(0, colon).trim().toLowerCase(Locale.ROOT);
            String value = line.substring(colon + 1).trim();
            if (name.equals("contact")) contacts.add(value);
            else if (name.equals("expires")) expires.add(value);
        }

        List<String> out = new ArrayList<>();
        if (contacts.isEmpty()) out.add("missing Contact");
        for (String c : contacts) {
            if (!(c.startsWith("mailto:") || c.startsWith("tel:") || c.startsWith("https://"))) {
                out.add("Contact must use mailto:, tel: or https://: " + c);
            }
        }
        if (expires.isEmpty()) {
            out.add("missing Expires");
        } else if (expires.size() > 1) {
            out.add("Expires must appear once, found " + expires.size());
        } else {
            Instant at = Instant.parse(expires.get(0));
            if (at.isBefore(now)) out.add("expired on " + at);
        }
        return out;
    }

    // CHECKS
    static final Instant NOW = Instant.parse("2026-11-21T12:00:00Z");

    public static void main(String[] args) {
        check("a valid file has no problems", List.of(), () -> problems(String.join("\n",
            "# Our security contacts",
            "Contact: mailto:security@example.com",
            "Contact: https://example.com/security",
            "",
            "Policy: https://example.com/disclosure-policy",
            "Expires: 2027-06-30T23:59:59Z"), NOW));
        check("field names are case-insensitive", List.of(),
            () -> problems("contact: tel:+1-201-555-0123\nEXPIRES: 2027-01-01T00:00:00Z", NOW));
        check("both required fields are reported missing", List.of("missing Contact", "missing Expires"),
            () -> problems("# nothing here\nPolicy: https://example.com/p", NOW));
        check("a plain http contact is rejected", List.of("Contact must use mailto:, tel: or https://: http://example.com/security"),
            () -> problems("Contact: http://example.com/security\nExpires: 2027-01-01T00:00:00Z", NOW));
        check("a stale file says when it expired", List.of("expired on 2025-12-31T18:37:07Z"),
            () -> problems("Contact: mailto:security@example.com\nExpires: 2025-12-31T18:37:07Z", NOW));
        check("Expires may appear only once", List.of("Expires must appear once, found 2"),
            () -> problems("Contact: mailto:a@example.com\nExpires: 2027-01-01T00:00:00Z\nExpires: 2028-01-01T00:00:00Z", NOW));
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
