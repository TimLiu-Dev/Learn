import java.util.Arrays;
import java.util.List;

// Exercise 1 solution: one reason to change per piece; same output as Ex1Before.
public class Ex1 {
    record LateReturn(String member, String title, int daysLate) {}
    record Row(LateReturn ret, int fineCents) {}

    static final class LateReturnCsv {                   // the input format
        static List<LateReturn> parse(String csv) {
            return Arrays.stream(csv.split("\n")).filter(l -> !l.isBlank()).map(l -> {
                String[] p = l.split(";");
                return new LateReturn(p[0], p[1], Integer.parseInt(p[2].trim()));
            }).toList();
        }
    }

    static final class FinePolicy {                      // the library's rule
        static final int CENTS_PER_DAY = 25, CAP_CENTS = 500;
        static int fineCents(LateReturn r) { return Math.min(r.daysLate() * CENTS_PER_DAY, CAP_CENTS); }
    }

    static final class StatementFormat {                 // the printed layout
        static String money(int cents) { return String.format("%d.%02d", cents / 100, cents % 100); }
        static String render(List<Row> rows) {
            StringBuilder sb = new StringBuilder();
            for (Row r : rows) sb.append(String.format("%-6s %-10s %s%n", r.ret().member(), r.ret().title(), money(r.fineCents())));
            return sb + "TOTAL " + money(rows.stream().mapToInt(Row::fineCents).sum());
        }
    }

    static String statement(String csv) {
        return StatementFormat.render(LateReturnCsv.parse(csv).stream().map(r -> new Row(r, FinePolicy.fineCents(r))).toList());
    }

    public static void main(String[] args) { System.out.println(statement("Ada;Dune;3\nAlan;Mort;40\nGrace;Emma;0\n")); }
}
