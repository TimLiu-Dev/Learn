import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class Idor {
    static final Map<String, String> TOKENS = Map.of("tok-ana", "ana", "tok-ben", "ben");  // stand-in for real sessions
    static final Map<String, String> ORDERS = Map.of("ana", "[\"book\"]", "ben", "[\"laptop\"]");

    static void reply(HttpExchange ex, int status, String body) throws IOException {
        byte[] bytes = body.getBytes();
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = ex.getResponseBody()) { out.write(bytes); }
    }

    // /<mode>/users/{name}/orders
    static void handle(HttpExchange ex, boolean checkOwner) throws IOException {
        String caller = TOKENS.get(ex.getRequestHeaders().getFirst("X-Token"));
        if (caller == null) { reply(ex, 401, "who are you?"); return; }           // not authenticated
        String owner = ex.getRequestURI().getPath().split("/")[3];
        if (checkOwner && !owner.equals(caller)) { reply(ex, 404, "not found"); return; }  // not yours
        reply(ex, 200, ORDERS.getOrDefault(owner, "[]"));
    }

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/unsafe/users/", ex -> handle(ex, false));
        server.createContext("/safe/users/", ex -> handle(ex, true));
        server.start();
        try {
            HttpClient client = HttpClient.newHttpClient();
            int port = server.getAddress().getPort();
            System.out.println("ben reads his own (unsafe):  " + get(client, port, "/unsafe/users/ben/orders", "tok-ben"));
            System.out.println("ben reads ana's   (unsafe):  " + get(client, port, "/unsafe/users/ana/orders", "tok-ben"));
            System.out.println("ben reads ana's   (safe):    " + get(client, port, "/safe/users/ana/orders", "tok-ben"));
            System.out.println("ben reads his own (safe):    " + get(client, port, "/safe/users/ben/orders", "tok-ben"));
            System.out.println("no token          (safe):    " + get(client, port, "/safe/users/ben/orders", null));
        } finally {
            server.stop(0);
        }
    }

    static String get(HttpClient client, int port, String path, String token) throws Exception {
        HttpRequest.Builder req = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path));
        if (token != null) req.header("X-Token", token);
        HttpResponse<String> res = client.send(req.build(), HttpResponse.BodyHandlers.ofString());
        return res.statusCode() + " " + res.body();
    }
}
