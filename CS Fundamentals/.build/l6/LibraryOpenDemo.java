import java.util.ArrayList;
import java.util.List;

public class LibraryOpenDemo {
    public static void main(String[] args) {
        Library lib = new Library();
        lib.add("  Dune ");
        lib.titles.add("  EMMA");                 // skips normalize: the invariant breaks
        lib.titles.clear();                       // or wipes everything from outside
        System.out.println(lib.titles.size());
    }
}

class Library {
    public List<String> titles = new ArrayList<>();   // everything public

    public void add(String title) { titles.add(normalize(title)); }

    public String normalize(String title) { return title.trim().toLowerCase(); }
}
