import java.util.*;

public class CaseOrder {
    public static void main(String[] args) {
        // A trie's children in a TreeMap<Character, ...>: keys come out in char order.
        TreeMap<Character, String> children = new TreeMap<>();
        for (String w : List.of("bee", "Bat", "ant")) children.put(w.charAt(0), w);
        System.out.println(children.values());
    }
}
