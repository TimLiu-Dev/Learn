import java.util.*;
import java.util.zip.CRC32;

public class Practice {
    record Recovered(Map<String, String> data, int validLength) {}

    // CRC-32 of the payload's UTF-8 bytes, as exactly 8 lowercase hex digits.
    static String crcHex(String payload) {
        CRC32 crc = new CRC32();
        crc.update(payload.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return String.format("%08x", crc.getValue());   // pad: a CRC can start with zero digits
    }

    // How the writer builds each record. Keys contain no '=' and no newlines.
    static String encode(String key, String value) {
        String payload = key + "=" + value;
        return crcHex(payload) + " " + payload + "\n";
    }

    static Recovered recover(String log) {
        Map<String, String> data = new HashMap<>();
        int pos = 0;                                     // end of the last good record
        while (true) {
            int end = log.indexOf('\n', pos);
            if (end < 0) break;                          // no newline: the record was never finished
            String line = log.substring(pos, end);
            if (line.length() <= 9 || line.charAt(8) != ' ') break;
            String payload = line.substring(9);
            int eq = payload.indexOf('=');
            if (eq < 0 || !crcHex(payload).equals(line.substring(0, 8))) break;   // nothing after it can be trusted
            data.put(payload.substring(0, eq), payload.substring(eq + 1));
            pos = end + 1;
        }
        return new Recovered(data, pos);
    }

    // CHECKS
    static String three() { return encode("k1", "v1") + encode("k2", "v2") + encode("k1", "v3"); }

    static String show(Recovered r) { return new TreeMap<>(r.data()) + " " + r.validLength(); }

    public static void main(String[] args) {
        check("crcHex is the CRC-32 as 8 hex digits", "3610a686", () -> crcHex("hello"));
        check("a CRC that starts with a zero digit still has 8 digits", "019cc60d", () -> crcHex("dune"));
        check("a clean log recovers every record and a later value wins", "{k1=v3, k2=v2} " + three().length(), () -> show(recover(three())));
        check("a cut anywhere inside the last record drops only that record", List.of(), () -> {
            int start = three().length() - encode("k1", "v3").length();
            List<Integer> wrong = new ArrayList<>();
            for (int cut = start; cut < three().length(); cut++) {
                if (!show(recover(three().substring(0, cut))).equals("{k1=v1, k2=v2} " + start)) wrong.add(cut);
            }
            return wrong;                              // the cut lengths that recovered the wrong thing
        });
        check("a bad checksum ends the log, and nothing after it is trusted", "{k1=v1} " + encode("k1", "v1").length(),
            () -> show(recover(three().replace("k2=v2", "k2=v9"))));
        check("junk after the last record is ignored", "{k1=v3, k2=v2} " + three().length(),
            () -> show(recover(three() + "1f2e3d4c k9=\u0000\u0000\n")));
        check("a write appended after truncating survives the next recovery", "{k1=v1, k2=v2, k4=v4}", () -> {
            String torn = three().substring(0, three().length() - 4);
            String repaired = torn.substring(0, recover(torn).validLength()) + encode("k4", "v4");
            return new TreeMap<>(recover(repaired).data()).toString();
        });
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
