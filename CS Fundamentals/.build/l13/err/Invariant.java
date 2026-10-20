import java.util.ArrayList;
import java.util.List;

public class Invariant {
    public static void main(String[] args) {
        List<String> names = new ArrayList<>(List.of("Ada"));
        List<Object> things = names;              // would let you add an Integer to names
        things.add(42);
    }
}
