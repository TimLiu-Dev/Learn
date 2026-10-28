import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Ex1 {
    // Sticky routing: the same client always goes to the same backend.
    static String pick(String client, List<String> backends) {
        return backends.get(Math.floorMod(client.hashCode(), backends.size()));
    }

    public static void main(String[] args) {
        List<String> clients = List.of("ada", "bob", "cy", "dee", "eve", "fay", "gus", "hal", "ivy", "jo");
        Map<String, String> before = clients.stream().collect(Collectors.toMap(Function.identity(), c -> pick(c, List.of("b1", "b2", "b3"))));
        Map<String, String> after = clients.stream().collect(Collectors.toMap(Function.identity(), c -> pick(c, List.of("b1", "b3"))));  // b2 died
        for (String c : clients) {
            String note = before.get(c).equals(after.get(c)) ? ""
                : before.get(c).equals("b2") ? "  moved (its backend died)"
                : "  MOVED although its backend is fine";
            System.out.printf("%-4s %s -> %s%s%n", c, before.get(c), after.get(c), note);
        }
        long needless = clients.stream()
            .filter(c -> !before.get(c).equals(after.get(c)) && !before.get(c).equals("b2")).count();
        System.out.println("clients that lost their session for no reason: " + needless + " of " + clients.size());
    }
}
