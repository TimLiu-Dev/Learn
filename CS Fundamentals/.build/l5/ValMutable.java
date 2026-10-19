import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValMutable {
    public static void main(String[] args) {
        final List<String> names = new ArrayList<>(List.of("Ada"));
        names.add("Linus");                  // fine: final freezes the reference, not the list
        System.out.println(names);

        List<String> view = Collections.unmodifiableList(names);  // read-only VIEW
        names.add("Grace");
        System.out.println(view);            // sees the change

        List<String> snapshot = List.copyOf(names);               // unmodifiable COPY
        names.add("Alan");
        System.out.println(snapshot);
        System.out.println(names);

        view.add("Mallory");                 // the view refuses writes
    }
}
