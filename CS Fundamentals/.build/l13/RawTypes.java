import java.util.ArrayList;
import java.util.List;

public class RawTypes {
    static void addLegacy(List list) {           // raw type: List with no <...>
        list.add(42);                            // the compiler can't stop this
    }

    public static void main(String[] args) {
        List<String> names = new ArrayList<>();
        names.add("Ada");
        addLegacy(names);                        // compiles, with only a warning
        System.out.println(names);               // [Ada, 42]: the list is "polluted"
        String last = names.get(1);              // ClassCastException HERE, far from the cause
    }
}
