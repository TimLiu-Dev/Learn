import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Practice {
    // "192.168.1.151" -> one 32-bit int, with 192 in the top 8 bits
    static int parseIpv4(String text) {
        throw new UnsupportedOperationException("TODO: split on dots, check for 4 numbers in 0..255, shift each one in");
    }

    // 24 -> 0xffffff00: prefix ones, then zeros
    static int maskOf(int prefix) {
        throw new UnsupportedOperationException("TODO: ones on the left, zeros on the right; watch /0 and /32");
    }

    // Is ip inside the block, e.g. cidrContains("192.168.1.0/24", "192.168.1.77")?
    static boolean cidrContains(String cidr, String ip) {
        throw new UnsupportedOperationException("TODO: compare only the bits the mask keeps");
    }

    // CHECKS
    static boolean rejects(String text) {
        try {
            parseIpv4(text);
            return false;
        } catch (IllegalArgumentException expected) {
            return true;
        }
    }

    public static void main(String[] args) {
        check("an address becomes one 32-bit number", "c0a80197", () -> Integer.toHexString(parseIpv4("192.168.1.151")));
        check("bad addresses are rejected", List.of(true, true, true, true), () -> Stream.of("256.1.1.1", "1.2.3", "1.2.3.4.5", "a.b.c.d").map(Practice::rejects).toList());
        check("masks match ifconfig's netmasks", List.of("ffffff00", "ff000000", "ffffffff", "0"), () -> Stream.of(24, 8, 32, 0).map(p -> Integer.toHexString(maskOf(p))).toList());
        check("a slash-24 block holds 256 addresses", List.of(true, true, false), () -> List.of(cidrContains("192.168.1.0/24", "192.168.1.77"), cidrContains("192.168.1.0/24", "192.168.1.255"), cidrContains("192.168.1.0/24", "192.168.2.1")));
        check("RFC 1918's 172 block is a slash-12", List.of(true, false, false), () -> List.of(cidrContains("172.16.0.0/12", "172.31.255.255"), cidrContains("172.16.0.0/12", "172.32.0.1"), cidrContains("172.16.0.0/12", "172.15.0.1")));
        check("the slash-0 block contains every address", List.of(true, true), () -> List.of(cidrContains("0.0.0.0/0", "8.8.8.8"), cidrContains("0.0.0.0/0", "255.255.255.255")));
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
