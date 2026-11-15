import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Practice {
    // Stands in for the kernel: every call to it counts as one "system call".
    static class CountingStream extends InputStream {
        private final byte[] data;
        private final int maxPerCall;
        private int pos = 0;
        int calls = 0;

        CountingStream(byte[] data, int maxPerCall) { this.data = data; this.maxPerCall = maxPerCall; }
        CountingStream(byte[] data) { this(data, Integer.MAX_VALUE); }

        @Override public int read() {
            calls++;
            return pos < data.length ? data[pos++] & 0xFF : -1;
        }

        @Override public int read(byte[] b, int off, int len) {
            calls++;
            if (pos >= data.length) return -1;
            int n = Math.min(Math.min(len, maxPerCall), data.length - pos);   // may return fewer bytes than asked
            System.arraycopy(data, pos, b, off, n);
            pos += n;
            return n;
        }
    }

    static class MyBufferedStream extends InputStream {
        private final InputStream src;
        private final byte[] buf;
        private int pos = 0;     // index of the next byte to hand out
        private int count = 0;   // how many bytes of buf hold real data

        MyBufferedStream(InputStream src, int size) { this.src = src; this.buf = new byte[size]; }
        MyBufferedStream(InputStream src) { this(src, 8192); }

        @Override public int read() throws IOException {
            throw new UnsupportedOperationException("TODO: refill buf when pos == count, then return the next byte as 0..255");
        }
    }

    // CHECKS
    static List<Integer> drain(InputStream s) throws IOException {
        List<Integer> out = new ArrayList<>();
        for (int b = s.read(); b != -1; b = s.read()) out.add(b);
        return out;
    }

    static byte[] hundred() {
        byte[] d = new byte[100];
        for (int i = 0; i < 100; i++) d[i] = (byte) i;
        return d;
    }

    static List<Integer> upTo100() {
        List<Integer> l = new ArrayList<>();
        for (int i = 0; i < 100; i++) l.add(i);
        return l;
    }

    public static void main(String[] args) {
        check("every byte comes back in order", upTo100(), () -> drain(new MyBufferedStream(new CountingStream(hundred()), 16)));
        check("byte 0xFF is 255, not the end of the stream", List.of(1, 255, 2), () -> drain(new MyBufferedStream(new CountingStream(new byte[]{1, -1, 2}), 16)));
        check("100 bytes through a 16-byte buffer cost 8 calls", 8, () -> {
            CountingStream src = new CountingStream(hundred());
            drain(new MyBufferedStream(src, 16));
            return src.calls;
        });
        check("unbuffered, the same 100 bytes cost 101 calls", 101, () -> {
            CountingStream src = new CountingStream(hundred());
            drain(src);
            return src.calls;
        });
        check("short reads are handled", upTo100(), () -> drain(new MyBufferedStream(new CountingStream(hundred(), 5), 16)));
        check("an empty source is -1 straight away", -1, () -> new MyBufferedStream(new CountingStream(new byte[0])).read());
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
