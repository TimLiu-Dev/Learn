import java.util.List;
import java.util.Optional;

public class Scopes {
    // Effective Java Item 2: a builder, because Java has no `apply`
    static final class Server {
        final String host;
        final int port;
        final boolean tls;

        private Server(Builder b) { host = b.host; port = b.port; tls = b.tls; }

        static final class Builder {
            private String host = "localhost";
            private int port = 80;
            private boolean tls = false;
            Builder host(String h) { host = h; return this; }
            Builder port(int p) { port = p; return this; }
            Builder tls(boolean t) { tls = t; return this; }
            Server build() { return new Server(this); }
        }

        @Override public String toString() { return "Server(" + host + ":" + port + ", tls=" + tls + ")"; }
    }

    static Optional<String> findNickname(String name) {
        return name.equals("Margaret") ? Optional.of("Peggy") : Optional.empty();
    }

    public static void main(String[] args) {
        // let -> Optional.map (or an if != null)
        System.out.println("let: " + findNickname("Margaret").map(String::length).orElse(null));
        System.out.println("let on null: " + findNickname("Ada").map(String::length).orElse(null));

        // apply -> a builder
        Server server = new Server.Builder().host("example.com").port(443).tls(true).build();
        System.out.println("apply: " + server);

        // also -> a temporary variable
        List<Integer> unsorted = List.of(8080, 80, 443);
        System.out.println("also: sorting " + unsorted);
        List<Integer> ports = unsorted.stream().sorted().toList();
        System.out.println("sorted: " + ports);

        // run -> repeat the variable name
        String url = (server.tls ? "https" : "http") + "://" + server.host + ":" + server.port;
        System.out.println("run: " + url);

        // with -> a temporary variable
        StringBuilder sb = new StringBuilder();
        sb.append("ports=");
        sb.append(String.join("/", ports.stream().map(String::valueOf).toList()));
        String summary = sb.toString();
        System.out.println("with: " + summary);

        // takeIf / takeUnless -> a conditional expression
        Integer even = server.port % 2 == 0 ? server.port : null;
        Integer notEven = server.port % 2 == 0 ? null : server.port;
        System.out.println("takeIf: " + even + ", " + notEven);
    }
}
