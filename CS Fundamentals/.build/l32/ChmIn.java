import java.util.concurrent.ConcurrentHashMap;

public class ChmIn {
    public static void main(String[] args) {
        var books = new ConcurrentHashMap<Long, String>();
        books.put(1L, "Dune");
        System.out.println(books.contains(1L));       // legacy Hashtable method: checks VALUES
        System.out.println(books.contains("Dune"));
        System.out.println(books.containsKey(1L));
    }
}
