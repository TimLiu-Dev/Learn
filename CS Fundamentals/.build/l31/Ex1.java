import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

// Exercise 1: a raw HTTP client that frames the body by Content-Length. Needs TinyServer on :18131.
public class Ex1 {
    record Response(int status, String reason, Map<String, String> headers, String body) {}

    static String readLine(InputStream in) throws IOException {
        var buf = new ByteArrayOutputStream();
        for (int b = in.read(); b != -1 && b != '\n'; b = in.read()) if (b != '\r') buf.write(b);
        return buf.toString(StandardCharsets.UTF_8);
    }

    static Response rawGet(String host, int port, String path) throws IOException {
        try (var socket = new Socket(host, port)) {
            String req = "GET " + path + " HTTP/1.1\r\nHost: " + host + "\r\n\r\n";
            socket.getOutputStream().write(req.getBytes(StandardCharsets.US_ASCII));
            var in = new BufferedInputStream(socket.getInputStream());
            String[] status = readLine(in).split(" ", 3);           // HTTP/1.1 404 Not Found
            var headers = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
            for (String line = readLine(in); !line.isEmpty(); line = readLine(in)) {
                headers.put(line.substring(0, line.indexOf(':')).trim(), line.substring(line.indexOf(':') + 1).trim());
            }
            String len = headers.get("Content-Length");
            byte[] body = len != null ? in.readNBytes(Integer.parseInt(len)) : in.readAllBytes();
            return new Response(Integer.parseInt(status[1]), status[2], headers,
                    new String(body, StandardCharsets.UTF_8));
        }
    }

    public static void main(String[] args) throws IOException {
        for (String path : List.of("/", "/hello?name=Ex", "/old", "/nope")) {
            Response r = rawGet("127.0.0.1", 18131, path);
            String extra = r.headers().containsKey("location") ? "  location=" + r.headers().get("location") : "";
            System.out.printf("%-15s %d %-17s %d headers  body=%s%s%n", path, r.status(), r.reason(),
                    r.headers().size(), r.body().strip().isEmpty() ? "(empty)" : '"' + r.body().strip() + '"', extra);
        }
    }
}
