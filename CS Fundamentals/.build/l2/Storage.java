import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Storage {
    static long unitBytes(String unit) {
        if (unit.equals("KiB")) return 1024L;
        else if (unit.equals("MiB")) return 1024L * 1024;
        else return 1024L * 1024 * 1024;
    }

    public static void main(String[] args) {
        List<String> inputs = List.of("512 MiB", "2 GiB", "4 GiB", "1.5 KiB", "bogus");
        Pattern pattern = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(KiB|MiB|GiB)");
        long total = 0;

        for (String input : inputs) {
            Matcher m = pattern.matcher(input);
            if (!m.matches()) {
                System.out.println("can't parse: " + input);
                continue;
            }
            BigDecimal number = new BigDecimal(m.group(1));
            long bytes = number.multiply(BigDecimal.valueOf(unitBytes(m.group(2)))).longValue();
            System.out.println(input + " = " + bytes + " bytes");
            total += bytes;
        }

        double gib = total / (1024.0 * 1024 * 1024);
        System.out.println("total = " + total + " bytes (" + String.format("%.2f", gib) + " GiB)");
    }
}
