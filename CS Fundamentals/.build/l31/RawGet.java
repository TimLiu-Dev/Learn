import java.net.Socket;
import java.nio.charset.StandardCharsets;

// One HTTP request, typed by hand, over a plain TCP socket.
public class RawGet {
    public static void main(String[] args) throws Exception {
        String request = "GET / HTTP/1.1\r\n"
                + "Host: example.com\r\n"
                + "Connection: close\r\n"
                + "\r\n";                                  // blank line: no body follows
        try (var socket = new Socket("example.com", 80)) {
            socket.getOutputStream().write(request.getBytes(StandardCharsets.US_ASCII));
            byte[] all = socket.getInputStream().readAllBytes();   // until the server closes
            String text = new String(all, StandardCharsets.UTF_8);
            int split = text.indexOf("\r\n\r\n");
            String head = text.substring(0, split);
            String body = text.substring(split + 4);
            System.out.println("status line : " + head.lines().findFirst().orElseThrow());
            head.lines().skip(1)
                .filter(h -> h.matches("(?i)(content-type|transfer-encoding|connection):.*"))
                .forEach(h -> System.out.println("header      : " + h));
            System.out.println("header lines: " + (head.lines().count() - 1));
            String sizeLine = body.lines().findFirst().orElseThrow();
            System.out.println("body line 1 : " + sizeLine + "   <- chunk size in hex = "
                    + Integer.parseInt(sizeLine.trim(), 16) + " bytes");
            System.out.println("body line 2 : " + body.lines().skip(1).findFirst().orElseThrow().substring(0, 40) + "...");
            System.out.println("bytes read  : " + all.length);
        }
    }
}
