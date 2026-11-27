import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * A tiny interactive client for the chat's TCP protocol, the length-prefixed stand-in for nc (L30).
 *
 *   java tools/ChatClient.java 7000
 *
 * Every line you type is sent as one frame (4-byte big-endian length, then UTF-8).
 * Every frame the server sends is printed as "< text". Ctrl+D closes your side.
 */
public class ChatClient {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("usage: java ChatClient.java TCP_PORT");
            System.exit(2);
        }
        try (var socket = new Socket(InetAddress.getLoopbackAddress(), Integer.parseInt(args[0]))) {
            var in = new DataInputStream(socket.getInputStream());
            var printer = Thread.ofPlatform().daemon().start(() -> {
                try {
                    while (true) {
                        byte[] payload = new byte[in.readInt()];
                        in.readFully(payload);
                        System.out.println("< " + new String(payload, StandardCharsets.UTF_8));
                    }
                } catch (EOFException e) {
                    System.out.println("(server closed the connection)");
                } catch (Exception e) {
                    System.out.println("(" + e + ")");
                }
            });
            var out = new DataOutputStream(socket.getOutputStream());
            var keyboard = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            String line;
            while ((line = keyboard.readLine()) != null) {
                byte[] bytes = line.getBytes(StandardCharsets.UTF_8);
                out.writeInt(bytes.length);
                out.write(bytes);
                out.flush();
            }
            socket.shutdownOutput();
            printer.join(5000);
        }
    }
}
