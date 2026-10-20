import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ToInterface {
    static void printAll(Collection<String> items) {        // accepts ANY collection
        for (String item : items) System.out.print(item + " ");
        System.out.println();
    }

    public static void main(String[] args) {
        List<String> names = new ArrayList<>();             // declared as the interface
        names.addAll(List.of("pear", "apple", "pear"));
        printAll(names);
        Set<String> unique = new TreeSet<>(names);          // swap the implementation freely
        printAll(unique);
    }
}
