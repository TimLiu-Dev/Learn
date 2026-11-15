import java.util.Set;

// A release toggle: the farewell code ships in every build, switched off until it's finished.
public class Toggle {
    record Flags(Set<String> on) {
        boolean isOn(String name) { return on.contains(name); }
    }

    static String greet(String name, Flags flags) {
        String hello = "Hello, " + name;
        return flags.isOn("farewell") ? hello + ". Bye for now!" : hello;
    }

    public static void main(String[] args) {
        System.out.println(greet("Ada", new Flags(Set.of())));            // what users get today
        System.out.println(greet("Ada", new Flags(Set.of("farewell"))));  // what tests and the team see
    }
}
