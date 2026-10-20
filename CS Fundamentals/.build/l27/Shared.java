// An immutable value can be shared by any number of threads with no locks at all.
import java.util.ArrayList;
import java.util.List;

public class Shared {
    record Config(String name, List<String> hosts) { }          // looks immutable...

    record SafeConfig(String name, List<String> hosts) {
        SafeConfig {
            hosts = List.copyOf(hosts);                         // ...this one is: private, unmodifiable copy
        }
    }

    public static void main(String[] args) {
        List<String> hosts = new ArrayList<>(List.of("a.example", "b.example"));
        Config config = new Config("prod", hosts);
        SafeConfig safe = new SafeConfig("prod", hosts);

        hosts.add("evil.example");                              // someone keeps the original list

        System.out.println(config);
        System.out.println(safe);
        try {
            safe.hosts().add("x");
        } catch (UnsupportedOperationException e) {
            System.out.println("safe.hosts().add -> " + e.getClass().getSimpleName());
        }
    }
}
