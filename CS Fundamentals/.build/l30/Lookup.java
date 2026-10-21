import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Security;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Lookup {
    static List<InetAddress> timedLookup(String name) throws UnknownHostException {
        long start = System.nanoTime();
        List<InetAddress> all = Arrays.asList(InetAddress.getAllByName(name)); // OS resolver + cache
        double ms = (System.nanoTime() - start) / 1_000_000.0;
        String ips = all.stream().map(InetAddress::getHostAddress).collect(Collectors.joining(", "));
        System.out.printf("%-12s %5.1f ms  %s%n", name, ms, ips);
        return all;
    }

    public static void main(String[] args) throws UnknownHostException {  // checked in Java
        List<InetAddress> first = timedLookup("example.com");
        timedLookup("example.com");                          // second time: JVM cache, no DNS query
        long v6 = first.stream().filter(a -> a instanceof Inet6Address).count();
        System.out.println("IPv4: " + (first.size() - v6) + ", IPv6: " + v6);

        timedLookup("localhost");                            // answered from /etc/hosts
        System.out.println("loopback: " + InetAddress.getLoopbackAddress());

        try {
            InetAddress.getByName("no-such-host.invalid");   // .invalid never exists (RFC 6761)
        } catch (UnknownHostException e) {
            System.out.println("caught " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        System.out.println("networkaddress.cache.ttl = " + Security.getProperty("networkaddress.cache.ttl"));
        System.out.println("networkaddress.cache.negative.ttl = "
            + Security.getProperty("networkaddress.cache.negative.ttl"));
    }
}
