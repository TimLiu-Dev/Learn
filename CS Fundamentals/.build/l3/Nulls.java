import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Nulls {
    static String emailDomain(Map<String, String> contacts, String name) {
        String email = contacts.get(name);              // may be null
        if (email == null) return null;
        return email.substring(email.indexOf('@') + 1);
    }

    static Optional<String> emailDomainOpt(Map<String, String> contacts, String name) {
        return Optional.ofNullable(contacts.get(name))
                .map(e -> e.substring(e.indexOf('@') + 1));
    }

    public static void main(String[] args) {
        Map<String, String> contacts = new HashMap<>();  // Map.of() rejects null values
        contacts.put("ada", "ada@math.org");
        contacts.put("linus", null);
        contacts.put("grace", "grace@navy.mil");

        for (String name : List.of("ada", "linus", "grace", "alan")) {
            String domain = emailDomain(contacts, name);
            System.out.println(name + ": " + (domain != null ? domain : "(no email)"));
        }
        System.out.println(emailDomainOpt(contacts, "alan").orElse("(no email)"));

        String missing = contacts.get("alan");
        System.out.println(missing.length());          // compiles fine, crashes at runtime
    }
}
