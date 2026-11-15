import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Practice {
    static byte[] frame(String message) {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);   // count bytes, not characters
        return ByteBuffer.allocate(4 + bytes.length)               // a ByteBuffer is big-endian by default
            .putInt(bytes.length)
            .put(bytes)
            .array();
    }

    static class FrameDecoder {
        private byte[] pending = new byte[0];                       // received, but not yet a whole message

        List<String> feed(byte[] chunk) {
            byte[] joined = Arrays.copyOf(pending, pending.length + chunk.length);
            System.arraycopy(chunk, 0, joined, pending.length, chunk.length);
            pending = joined;
            List<String> messages = new ArrayList<>();
            while (pending.length >= 4) {                           // is the length prefix itself complete?
                int length = ByteBuffer.wrap(pending, 0, 4).getInt();
                if (pending.length < 4 + length) break;             // the rest hasn't arrived yet
                messages.add(new String(pending, 4, length, StandardCharsets.UTF_8));
                pending = Arrays.copyOfRange(pending, 4 + length, pending.length);
            }
            return messages;
        }
    }

    // CHECKS
    static byte[] concat(byte[]... parts) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (byte[] p : parts) out.writeBytes(p);
        return out.toByteArray();
    }

    static List<String> realSocket() throws Exception {
        byte[] wire = concat(frame("hello"), frame("wörld"), frame("bye"));
        InetAddress loopback = InetAddress.getLoopbackAddress();
        try (ServerSocket server = new ServerSocket(0, 50, loopback)) {
            Thread sender = new Thread(() -> {
                try (Socket s = server.accept()) { s.getOutputStream().write(wire); }   // closing = end of stream
                catch (IOException e) { throw new UncheckedIOException(e); }
            });
            sender.start();
            FrameDecoder decoder = new FrameDecoder();
            List<String> got = new ArrayList<>();
            try (Socket socket = new Socket(loopback, server.getLocalPort())) {
                socket.setSoTimeout(2_000);
                InputStream input = socket.getInputStream();
                byte[] buf = new byte[3];
                int n;
                while ((n = input.read(buf)) != -1) got.addAll(decoder.feed(Arrays.copyOf(buf, n)));
            }
            sender.join();
            return got;
        }
    }

    public static void main(String[] args) {
        check("a frame is a big-endian length then the bytes", "[0, 0, 0, 2, 104, 105]", () -> Arrays.toString(frame("hi")));
        check("the length counts bytes, not characters", "[0, 0, 0, 6]", () -> Arrays.toString(Arrays.copyOf(frame("héllo"), 4)));
        check("two messages in one read come out as two", List.of("hello", "world"), () -> new FrameDecoder().feed(concat(frame("hello"), frame("world"))));
        check("a message split across reads waits for the rest", List.of(List.of(), List.of(), List.of("hello")), () -> {
            byte[] bytes = frame("hello");
            FrameDecoder decoder = new FrameDecoder();
            return List.of(decoder.feed(Arrays.copyOfRange(bytes, 0, 2)), decoder.feed(Arrays.copyOfRange(bytes, 2, 7)), decoder.feed(Arrays.copyOfRange(bytes, 7, bytes.length)));
        });
        check("every way of cutting the stream gives the same messages", "all sizes OK", () -> {
            List<String> messages = List.of("hello", "", "héllo", "bye");
            byte[] wire = concat(messages.stream().map(Practice::frame).toArray(byte[][]::new));
            for (int size = 1; size <= wire.length; size++) {
                FrameDecoder decoder = new FrameDecoder();
                List<String> got = new ArrayList<>();
                for (int at = 0; at < wire.length; at += size) got.addAll(decoder.feed(Arrays.copyOfRange(wire, at, Math.min(at + size, wire.length))));
                if (!got.equals(messages)) return "reads of " + size + " bytes gave " + got;
            }
            return "all sizes OK";
        });
        check("a real socket, read 3 bytes at a time", List.of("hello", "wörld", "bye"), Practice::realSocket);
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
