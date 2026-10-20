import java.util.Optional;

public class Nested {
    record User(String name, String email) {}

    static User findUser(int id) {
        return switch (id) {
            case 1 -> new User("ada", "ada@example.com");
            case 2 -> new User("bob", null);
            default -> null;
        };
    }

    // Java's version of the chain: Optional.map skips the rest once a value is missing
    static String domainChained(int id) {
        return Optional.ofNullable(findUser(id))
            .map(User::email)
            .map(e -> "domain " + e.substring(e.indexOf('@') + 1))
            .orElse("no contact");
    }

    // The same logic with names and early returns
    static String domain(int id) {
        User user = findUser(id);
        if (user == null || user.email() == null) return "no contact";
        return "domain " + user.email().substring(user.email().indexOf('@') + 1);
    }

    public static void main(String[] args) {
        for (int id = 1; id <= 3; id++) System.out.println(id + ": " + domainChained(id) + " | " + domain(id));
    }
}
