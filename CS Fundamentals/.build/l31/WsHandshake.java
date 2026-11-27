import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;

// A WebSocket server on a raw socket, just enough to talk to the JDK's WebSocket client.
public class WsHandshake {
    static final String GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";   // fixed by RFC 6455

    static String accept(String key) throws Exception {
        byte[] sha1 = MessageDigest.getInstance("SHA-1").digest((key + GUID).getBytes(StandardCharsets.US_ASCII));
        return Base64.getEncoder().encodeToString(sha1);
    }

    public static void main(String[] args) throws Exception {
        String a = accept("dGhlIHNhbXBsZSBub25jZQ==");                    // RFC 6455's own example
        System.out.println("RFC example: " + a + " " + a.equals("s3pPLMBiTxaQ9kYGzzhZRbK+xOo="));

        CountDownLatch serverGotText = new CountDownLatch(1);
        try (ServerSocket listener = new ServerSocket(0, 50, java.net.InetAddress.getLoopbackAddress())) {
            Thread server = Thread.ofVirtual().start(() -> serve(listener, serverGotText));
            CompletableFuture<String> greeting = new CompletableFuture<>();
            WebSocket ws = HttpClient.newHttpClient().newWebSocketBuilder()
                .buildAsync(URI.create("ws://127.0.0.1:" + listener.getLocalPort() + "/chat"), new WebSocket.Listener() {
                    public CompletionStage<?> onText(WebSocket w, CharSequence text, boolean last) {
                        greeting.complete(text.toString());
                        w.request(1);                                      // ready for one more message
                        return null;
                    }
                }).join();                                                 // returns after the 101
            System.out.println("client got : " + greeting.join());
            ws.sendText("hi from java.net.http", true).join();
            serverGotText.await();
            ws.sendClose(WebSocket.NORMAL_CLOSURE, "bye").join();
            server.join();                                                 // let the server print its last line
        }
    }

    static void serve(ServerSocket listener, CountDownLatch gotText) {
        try (Socket s = listener.accept()) {
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.ISO_8859_1));
            String key = null;
            for (String line = in.readLine(); !line.isEmpty(); line = in.readLine()) {
                System.out.println("  > " + line);
                if (line.toLowerCase().startsWith("sec-websocket-key:")) key = line.substring(18).trim();
            }
            String acc = accept(Objects.requireNonNull(key, "not a WebSocket request"));
            OutputStream out = s.getOutputStream();
            out.write(("HTTP/1.1 101 Switching Protocols\r\nUpgrade: websocket\r\nConnection: Upgrade\r\n"
                + "Sec-WebSocket-Accept: " + acc + "\r\n\r\n").getBytes(StandardCharsets.US_ASCII));
            System.out.println("  < HTTP/1.1 101 Switching Protocols, Sec-WebSocket-Accept: " + acc);
            byte[] hello = "hello from a raw socket".getBytes(StandardCharsets.UTF_8);
            out.write(new byte[] {(byte) 0x81, (byte) hello.length});      // FIN + text opcode; unmasked, short length
            out.write(hello);
            out.flush();
            // The client sends no frame before it has our 101, so the reader above holds no frame bytes.
            DataInputStream din = new DataInputStream(s.getInputStream());
            while (true) {
                int b0 = din.readUnsignedByte(), b1 = din.readUnsignedByte();
                byte[] mask = din.readNBytes(4);                           // clients MUST mask
                byte[] payload = din.readNBytes(b1 & 0x7F);
                for (int i = 0; i < payload.length; i++) payload[i] ^= mask[i % 4];
                System.out.printf("  frame %02x %02x: opcode=%d masked=%b length=%d%n",
                    b0, b1, b0 & 0x0F, (b1 & 0x80) != 0, b1 & 0x7F);
                if ((b0 & 0x0F) == 1) {
                    System.out.println("server got : " + new String(payload, StandardCharsets.UTF_8));
                    gotText.countDown();
                } else if ((b0 & 0x0F) == 8) {
                    int code = ((payload[0] & 0xFF) << 8) | (payload[1] & 0xFF);
                    System.out.println("server got : close " + code + " " + new String(payload, 2, payload.length - 2, StandardCharsets.UTF_8));
                    out.write(new byte[] {(byte) 0x88, 0});                // answer the close, then hang up
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("server: " + e);
        }
    }
}
