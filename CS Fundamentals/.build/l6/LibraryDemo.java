import java.util.ArrayList;
import java.util.List;

public class LibraryDemo {
    public static void main(String[] args) {
        Library lib = new Library();
        lib.add("  Dune ");
        lib.add("EMMA");
        System.out.println(lib.count());
        System.out.println(lib.has("dune"));
        System.out.println(lib.has("Ulysses"));
    }
}

final class Library {
    private final List<String> titles = new ArrayList<>();   // internal state: hidden

    public int count() { return titles.size(); }

    public void add(String title) { titles.add(normalize(title)); }

    public boolean has(String title) { return titles.contains(normalize(title)); }

    private String normalize(String title) { return title.trim().toLowerCase(); }   // a detail
}
