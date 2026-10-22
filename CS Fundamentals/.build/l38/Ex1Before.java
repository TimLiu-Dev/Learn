// Exercise 1 starting point: a fines statement that does everything in one method.
public class Ex1Before {
    static String statement(String csv) {
        StringBuilder sb = new StringBuilder();
        int total = 0;
        for (String line : csv.split("\n")) {
            if (line.isBlank()) continue;
            String[] p = line.split(";");                                    // parse
            int fine = Math.min(Integer.parseInt(p[2].trim()) * 25, 500);    // rule: 25 cents a day, capped at 5.00
            total += fine;
            sb.append(String.format("%-6s %-10s %d.%02d%n", p[0], p[1], fine / 100, fine % 100));   // format
        }
        sb.append(String.format("TOTAL %d.%02d", total / 100, total % 100));
        return sb.toString();
    }

    public static void main(String[] args) { System.out.println(statement("Ada;Dune;3\nAlan;Mort;40\nGrace;Emma;0\n")); }
}
