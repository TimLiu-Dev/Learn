import java.util.*;

public class LatestTag {
    public static void main(String[] args) {
        List<String> tags = new ArrayList<>(List.of("1.2.0", "1.10.0", "1.9.1"));
        Collections.sort(tags);
        System.out.println("latest: " + tags.get(tags.size() - 1));
    }
}
