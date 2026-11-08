import java.util.*;

public class Practice {
    static final class Email {
        final String address;
        Email(String address) { this.address = address; }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Email other)) return false;
            return address.equalsIgnoreCase(other.address);
        }

        // Locale.ROOT: lower-casing must not depend on the computer's language settings.
        @Override public int hashCode() { return address.toLowerCase(Locale.ROOT).hashCode(); }

        @Override public String toString() { return "Email(" + address + ")"; }
    }

    // CHECKS
    public static void main(String[] args) {
        check("letter case does not matter", true, () -> new Email("ana@x.io").equals(new Email("Ana@X.io")));
        check("different addresses differ", false, () -> new Email("ana@x.io").equals(new Email("bob@x.io")));
        check("an email never equals a String", false, () -> new Email("ana@x.io").equals("ana@x.io"));
        check("an email never equals null", false, () -> new Email("ana@x.io").equals(null));
        check("equal emails have equal hash codes", true, () -> new Email("ana@x.io").hashCode() == new Email("ANA@X.IO").hashCode());
        check("a hash set keeps one per address", 1, () -> new HashSet<>(List.of(new Email("Ana@X.io"), new Email("ana@x.io"), new Email("ANA@x.IO"))).size());
        check("a map finds a value by an equal key", "Ana", () -> Map.of(new Email("ana@x.io"), "Ana").get(new Email("Ana@X.io")));
        check("toString shows the address as typed", "Email(Ana@X.io)", () -> new Email("Ana@X.io").toString());
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
