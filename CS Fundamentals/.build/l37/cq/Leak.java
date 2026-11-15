import java.util.ArrayList;
import java.util.List;

public class Leak {
    static class Shelf {
        private final List<String> titles = new ArrayList<>();

        void add(String title) { titles.add(title); }
        List<String> titles() { return titles; }
        List<String> snapshot() { return List.copyOf(titles); }
    }

    public static void main(String[] args) {
        Shelf shelf = new Shelf();
        shelf.add("Dune");
        shelf.titles().add("Emma");
        try {
            shelf.snapshot().add("Kindred");
        } catch (UnsupportedOperationException e) {
            System.out.print("refused ");
        }
        System.out.println(shelf.titles().size());
    }
}
