import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Startup {
    public static void main(String[] args) {
        var words = Pattern.compile("\\W+").splitAsStream("the cat sat on the mat the end")
                .collect(Collectors.groupingBy(w -> w, TreeMap::new, Collectors.counting()));
        var date = LocalDate.of(2026, 10, 20).format(DateTimeFormatter.ofPattern("EEEE d MMMM yyyy"));
        System.out.println(words + " " + date);
    }
}
