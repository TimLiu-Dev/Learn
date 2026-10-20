import java.util.ArrayList;
import java.util.List;

public class ShallowCopy {
    record Team(String name, List<String> members) {}

    public static void main(String[] args) {
        Team original = new Team("Core", new ArrayList<>(List.of("Ada")));
        Team renamed = new Team("Platform", original.members());   // Java's "copy": new record, same list
        renamed.members().add("Linus");
        System.out.println(original);
        System.out.println(renamed);
        System.out.println(original.equals(renamed));
    }
}
