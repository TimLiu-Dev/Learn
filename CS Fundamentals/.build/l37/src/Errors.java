import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Errors {
    public static void main(String[] args) throws IOException {
        String title = "Dune";
        System.out.println(title.substring(0, Math.min(10, title.length())));   // clamp by hand
        try { title.substring(0, 10); } catch (StringIndexOutOfBoundsException e) { System.out.println(e); }

        List<String> shelf = List.of("Dune", "Emma");
        System.out.println(5 < shelf.size() ? shelf.get(5) : null);             // no getOrNull: write it
        try { shelf.get(5); } catch (IndexOutOfBoundsException e) { System.out.println(e); }

        Map<Long, String> openLoans = new HashMap<>(Map.of(1L, "Ada"));
        System.out.println(openLoans.remove(2L));                               // removing nothing is not an error

        Path missing = Path.of(System.getProperty("java.io.tmpdir"), "l37-does-not-exist.txt");
        System.out.println(Files.deleteIfExists(missing));                      // false, no exception
        try { Files.delete(missing); } catch (IOException e) { System.out.println(e.getClass().getSimpleName()); }
    }
}
