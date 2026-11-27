import java.io.ByteArrayOutputStream;

// Protocol Buffers' base-128 varint, by hand: 7 bits per byte, low group first, top bit = "more follows".
public class Varint {
    static byte[] encodeVarint(long value) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        long v = value;
        while (true) {
            int low7 = (int) (v & 0x7F);
            v >>>= 7;                                      // unsigned shift (L48)
            if (v == 0) { out.write(low7); return out.toByteArray(); }
            out.write(low7 | 0x80);                        // set the continuation bit
        }
    }

    static long decodeVarint(byte[] bytes) {
        long result = 0;
        for (int i = 0; i < bytes.length; i++) {
            result |= (bytes[i] & 0x7FL) << (7 * i);
            if ((bytes[i] & 0x80) == 0) break;             // no continuation bit: last byte
        }
        return result;
    }

    static String hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(sb.isEmpty() ? "" : " ").append(String.format("%02x", b));
        return sb.toString();
    }

    public static void main(String[] args) {
        for (long n : new long[] {1, 150, 300}) {
            byte[] bytes = encodeVarint(n);
            System.out.println(String.format("%3d -> %-5s -> %d", n, hex(bytes), decodeVarint(bytes)));
        }
        int tag = (1 << 3) | 0;                            // field number 1, wire type 0 (VARINT)
        byte[] v = encodeVarint(150);
        byte[] message = new byte[1 + v.length];
        message[0] = (byte) tag;
        System.arraycopy(v, 0, message, 1, v.length);
        System.out.println("field 1 = 150 : " + hex(message) + "  (" + message.length + " bytes)");
        System.out.println("JSON {\"a\":150}: " + "{\"a\":150}".length() + " bytes");
    }
}
