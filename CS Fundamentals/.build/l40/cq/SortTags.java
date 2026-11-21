import java.util.*;

public class SortTags {
    public static void main(String[] args) {
        List<String> tags = new ArrayList<>(List.of("1.2.0", "1.10.0", "1.10.0-rc.1", "1.9.1"));
        Collections.sort(tags);
        System.out.println(tags);
    }
}
