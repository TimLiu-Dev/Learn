import java.util.*;

public class Practice {
    static final int HLT = 0x0, LDI = 0x1, LD = 0x2, ST = 0x3;
    static final int ADD = 0x4, SUB = 0x5, JMP = 0x6, JZ = 0x7;

    // "LD  R1, [80]" -> [LD, R1, 80]
    static List<String> tokens(String line) {
        List<String> t = new ArrayList<>();
        for (String s : line.trim().split("[\\s,\\[\\]]+")) if (!s.isEmpty()) t.add(s);
        return t;
    }

    static int reg(String t) { return Integer.parseInt(t.substring(1)); }   // "R2" -> 2
    static int num(String t) { return Integer.parseInt(t, 16); }           // "0E" -> 14

    static int[] encode(int op, int rd, int rs, int b1) {
        return new int[] { op << 4 | rd << 2 | rs, b1 };                   // opcode | rd | rs, then the second byte
    }

    // One line of toy assembly -> its two bytes.
    static int[] assemble(String line) {
        List<String> t = tokens(line);
        return switch (t.get(0)) {
            case "HLT" -> encode(HLT, 0, 0, 0);
            case "LDI" -> encode(LDI, reg(t.get(1)), 0, num(t.get(2)));
            case "LD" -> encode(LD, reg(t.get(1)), 0, num(t.get(2)));
            case "ST" -> encode(ST, reg(t.get(1)), 0, num(t.get(2)));
            case "ADD" -> encode(ADD, reg(t.get(1)), reg(t.get(2)), 0);
            case "SUB" -> encode(SUB, reg(t.get(1)), reg(t.get(2)), 0);
            case "JMP" -> encode(JMP, 0, 0, num(t.get(1)));
            case "JZ" -> encode(JZ, reg(t.get(1)), 0, num(t.get(2)));
            default -> throw new IllegalArgumentException("unknown mnemonic " + t.get(0));
        };
    }

    // CHECKS
    static String bytes(String line) {
        int[] b = assemble(line);
        return String.format("%02X %02X", b[0], b[1]);
    }

    // The lesson's disassembler, used to check that assemble undoes it.
    static String disassemble(int b0, int b1) {
        int rd = (b0 >>> 2) & 3, rs = b0 & 3;
        String n = String.format("%02X", b1);
        return switch (b0 >>> 4) {
            case HLT -> "HLT";
            case LDI -> "LDI R" + rd + ", " + n;
            case LD -> "LD  R" + rd + ", [" + n + "]";
            case ST -> "ST  R" + rd + ", [" + n + "]";
            case ADD -> "ADD R" + rd + ", R" + rs;
            case SUB -> "SUB R" + rd + ", R" + rs;
            case JMP -> "JMP " + n;
            case JZ -> "JZ  R" + rd + ", " + n;
            default -> "???";
        };
    }

    public static void main(String[] args) {
        check("SUB R2, R3 is 5B, not 5E", "5B 00", () -> bytes("SUB R2, R3"));
        check("numbers and addresses are hex", "18 01 / 24 80 / 74 0E", () -> bytes("LDI R2, 01") + " / " + bytes("LD R1, [80]") + " / " + bytes("JZ R1, 0E"));
        check("JMP and HLT have no register", "60 06 / 00 00", () -> bytes("JMP 06") + " / " + bytes("HLT"));
        check("the sum program assembles to the lesson's bytes",
              "10 00 24 80 18 01 74 0E 41 00 56 00 60 06 30 81 00 00", () -> {
            List<String> parts = new ArrayList<>();
            for (String line : List.of("LDI R0, 00", "LD R1, [80]", "LDI R2, 01", "JZ R1, 0E",
                    "ADD R0, R1", "SUB R1, R2", "JMP 06", "ST R0, [81]", "HLT")) parts.add(bytes(line));
            return String.join(" ", parts);
        });
        check("assemble undoes the disassembler for every instruction", 0, () -> {
            int wrong = 0;
            for (int op = 0; op <= 7; op++) for (int rd = 0; rd <= 3; rd++) for (int rs = 0; rs <= 3; rs++) {
                int b0 = switch (op) {
                    case HLT, JMP -> op << 4;
                    case ADD, SUB -> op << 4 | rd << 2 | rs;
                    default -> op << 4 | rd << 2;
                };
                int b1 = (op == ADD || op == SUB || op == HLT) ? 0 : 0xA7;
                if (!bytes(disassemble(b0, b1)).equals(String.format("%02X %02X", b0, b1))) wrong++;
            }
            return wrong;
        });
        check("an unknown mnemonic is rejected", "IllegalArgumentException", () -> {
            try { assemble("MUL R0, R1"); return "no exception"; }
            catch (IllegalArgumentException e) { return "IllegalArgumentException"; }
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
