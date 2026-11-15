import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Practice {
    static int parseIpv4(String text) {
        String[] parts = text.split("\\.");             // a regex: "." alone would match every character
        if (parts.length != 4) throw new IllegalArgumentException("an IPv4 address has 4 numbers: " + text);
        int ip = 0;
        for (String part : parts) {
            int n = Integer.parseInt(part);             // NumberFormatException is an IllegalArgumentException
            if (n < 0 || n > 255) throw new IllegalArgumentException(part + " is not in 0..255");
            ip = (ip << 8) | n;                         // make room, then drop the next 8 bits in
        }
        return ip;
    }

    static int maskOf(int prefix) {
        if (prefix < 0 || prefix > 32) throw new IllegalArgumentException("prefix must be 0..32: " + prefix);
        // An int shift uses only the low 5 bits of the distance, so -1 << 32 is still -1.
        return prefix == 0 ? 0 : -1 << (32 - prefix);
    }

    static boolean cidrContains(String cidr, String ip) {
        String[] parts = cidr.split("/");
        int mask = maskOf(Integer.parseInt(parts[1]));
        return (parseIpv4(ip) & mask) == (parseIpv4(parts[0]) & mask);
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
