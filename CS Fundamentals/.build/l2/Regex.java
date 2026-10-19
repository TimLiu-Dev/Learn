import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
    public static void main(String[] args) {
        String text = "Order 66 shipped 3 items for 120 dollars";
        Pattern digits = Pattern.compile("\\d+");     // backslash must be escaped in a Java string

        Matcher m = digits.matcher(text);
        if (m.find()) System.out.println(m.group());  // first match
        m = digits.matcher(text);
        while (m.find()) System.out.print(m.group() + " ");
        System.out.println();

        System.out.println("ID-1234".matches("ID-\\d{4}"));
        System.out.println("ID-12".matches("ID-\\d{4}"));
        System.out.println("a1b22c333".replaceAll("\\d+", "#"));

        Matcher d = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})").matcher("2026-10-18");
        if (d.matches()) System.out.println(d.group(0) + " " + d.group(1) + " " + d.group(2) + " " + d.group(3));
    }
}
