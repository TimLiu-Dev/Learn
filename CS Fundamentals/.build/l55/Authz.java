import java.util.*;
import java.util.stream.Collectors;

// Authorization: one function decides, every request asks it, and anything it doesn't know is denied.
public class Authz {
    enum Role { READER, EDITOR, ADMIN }
    enum Action { READ, CREATE, UPDATE, DELETE, PUBLISH }

    record User(String name, Set<Role> roles) {}
    record Book(long id, String owner) {}

    // RBAC: roles grant permissions. "own" permissions also need the relationship check below.
    static final Map<Role, Set<String>> permissions = Map.of(
            Role.READER, Set.of("book:read"),
            Role.EDITOR, Set.of("book:read", "book:create", "book:update:own", "book:delete:own"),
            Role.ADMIN, Set.of("book:read", "book:create", "book:update:any", "book:delete:any"));

    static boolean can(User user, Action action, Book book) {
        if (action == Action.READ) return true;                       // the catalogue is public
        if (user == null) return false;                               // everything else needs a login
        Set<String> granted = user.roles().stream()
                .flatMap(r -> permissions.getOrDefault(r, Set.of()).stream()).collect(Collectors.toSet());
        String verb = action.name().toLowerCase();
        if (granted.contains("book:" + verb + ":any")) return true;
        if (granted.contains("book:" + verb + ":own"))
            return book != null && book.owner().equals(user.name());  // ownership, not just role
        return granted.contains("book:" + verb);                      // no rule matched: deny by default
    }

    public static void main(String[] args) {
        var anasBook = new Book(1, "ana");
        List<User> people = Arrays.asList(
                null,
                new User("ben", Set.of(Role.READER)),
                new User("ana", Set.of(Role.EDITOR)),
                new User("cy", Set.of(Role.EDITOR)),
                new User("root", Set.of(Role.ADMIN)));
        var header = new StringBuilder("on ana's book   ");
        for (var a : Action.values()) header.append(String.format("%-9s", a));
        System.out.println(header);
        for (var u : people) {
            var label = u == null ? "anonymous" : u.name() + " (" + u.roles().iterator().next().name().toLowerCase() + ")";
            var row = new StringBuilder(String.format("%-16s", label));
            for (var a : Action.values()) row.append(String.format("%-9s", can(u, a, anasBook) ? "yes" : "-"));
            System.out.println(row);
        }
    }
}
