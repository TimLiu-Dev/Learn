import java.util.*;

public class Seq {
    public static void main(String[] args) {
        List<String> steps = new ArrayList<>(List.of("compile", "test", "jar"));
        List<String> backwards = steps.reversed();       // a view, not a copy (Java 21+)
        steps.addLast("publish");
        System.out.println(steps.getFirst() + " .. " + steps.getLast() + "  reversed " + backwards);

        LinkedHashMap<String, Integer> seen = new LinkedHashMap<>();
        seen.put("ada", 3); seen.put("ben", 1); seen.put("cy", 2);
        seen.putFirst("cy", 5);                           // moves cy to the front
        System.out.println(seen.firstEntry() + " .. " + seen.lastEntry() + "  " + seen.sequencedKeySet().reversed());
    }
}
