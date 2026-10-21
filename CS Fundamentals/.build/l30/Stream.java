import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// TCP delivers a stream of bytes. It does not remember where one write() ended.
public class Stream {
    public static void main(String[] args) throws Exception {
        InetAddress loopback = InetAddress.getLoopbackAddress();
        List<String> messages = List.of("hello", "world", "bye");
        try (ServerSocket server = new ServerSocket(0, 50, loopback)) {
            // 1. Three writes, then the receiver does ONE read
            try (Socket c = new Socket(loopback, server.getLocalPort()); Socket s = server.accept()) {
                for (String m : messages) c.getOutputStream().write(m.getBytes());  // 3 writes
                Thread.sleep(100);                             // let all the bytes arrive
                byte[] buf = new byte[1024];
                int n = s.getInputStream().read(buf);
                System.out.println("1. one read got " + n + " bytes: \"" + new String(buf, 0, n) + "\"");
            }
            // 2. The same three writes, read with a 4-byte buffer
            try (Socket c = new Socket(loopback, server.getLocalPort()); Socket s = server.accept()) {
                for (String m : messages) c.getOutputStream().write(m.getBytes());
                c.shutdownOutput();                            // tell the reader: no more bytes
                byte[] buf = new byte[4];
                InputStream input = s.getInputStream();
                List<String> chunks = new ArrayList<>();
                int n;
                while ((n = input.read(buf)) != -1) chunks.add(new String(buf, 0, n)); // -1 = end
                System.out.println("2. 4-byte reads got: " + chunks);
            }
            // 3. Framing: send each message as [length][bytes], read back exactly that many
            try (Socket c = new Socket(loopback, server.getLocalPort()); Socket s = server.accept()) {
                DataOutputStream out = new DataOutputStream(c.getOutputStream());
                for (String m : messages) {
                    byte[] bytes = m.getBytes(StandardCharsets.UTF_8);
                    out.writeInt(bytes.length);                // 4-byte big-endian length prefix
                    out.write(bytes);
                }
                out.flush();
                DataInputStream input = new DataInputStream(s.getInputStream());
                List<String> got = new ArrayList<>();
                for (int i = 0; i < messages.size(); i++) {
                    byte[] bytes = new byte[input.readInt()];  // how long is the next message?
                    input.readFully(bytes);                    // keep reading until we have it all
                    got.add(new String(bytes, StandardCharsets.UTF_8));
                }
                System.out.println("3. length-prefixed frames: " + got);
            }
        }
    }
}
