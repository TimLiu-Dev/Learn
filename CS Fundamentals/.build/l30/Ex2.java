import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

// Resolve each name twice and time it. The second pass shows the JVM's caches,
// including the 10-second negative cache for names that don't exist.
public class Ex2 {
    public static void main(String[] args) {
        List<String> names = List.of("example.com", "example.org", "localhost", "nope.invalid");
        for (int pass = 1; pass <= 2; pass++) {
            System.out.println("pass " + pass);
            for (String name : names) {
                long start = System.nanoTime();
                String result;
                try {
                    InetAddress[] all = InetAddress.getAllByName(name);
                    long v6 = java.util.Arrays.stream(all).filter(a -> a instanceof Inet6Address).count();
                    result = (all.length - v6) + " IPv4 + " + v6 + " IPv6";
                } catch (UnknownHostException e) {
                    result = "unknown host";
                }
                System.out.printf("  %-12s %-16s %6.2f ms%n", name, result, (System.nanoTime() - start) / 1e6);
            }
        }
    }
}
