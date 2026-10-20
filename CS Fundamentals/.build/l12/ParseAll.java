import java.util.ArrayList;
import java.util.List;

public class ParseAll {
    public static void main(String[] args) {
        List<String> lines = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) lines.add(i == 641 ? "64l" : String.valueOf(i));   // one typo hiding in 1000 lines

        long sum = 0;
        for (int index = 0; index < lines.size(); index++) {
            sum += Integer.parseInt(lines.get(index));
        }
        System.out.println("sum: " + sum);
    }
}
