import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lsp {
    // A method written against the base type: List<String> promises add().
    static void addDefaults(List<String> tags) {
        tags.add("new");
        tags.add("unread");
    }

    public static void main(String[] args) {
        List<String> tags = new ArrayList<>(List.of("sci-fi"));
        addDefaults(tags);
        System.out.println("ArrayList:      " + tags);

        for (List<String> fixed : List.of(List.of("sci-fi"), Collections.unmodifiableList(new ArrayList<>(List.of("sci-fi"))))) {
            try {
                addDefaults(fixed);                         // compiles: both ARE Lists
            } catch (UnsupportedOperationException e) {
                System.out.println(fixed.getClass().getSimpleName() + ": " + e);
            }
        }
    }
}
