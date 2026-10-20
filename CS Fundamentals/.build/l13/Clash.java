import java.util.List;

public class Clash {
    static String labelInts(List<Integer> xs) { return "ints: " + xs; }      // Java's only fix:
    static String labelStrings(List<String> xs) { return "strings: " + xs; } // different names

    public static void main(String[] args) {
        System.out.println(labelInts(List.of(1, 2)));
        System.out.println(labelStrings(List.of("a", "b")));
    }
}
