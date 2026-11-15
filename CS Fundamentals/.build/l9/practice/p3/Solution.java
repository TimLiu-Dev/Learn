import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Practice {
    static class Playlist {
        public final String name;                       // @JvmField: a public field, no getter
        private final List<String> tracks = new ArrayList<>();

        Playlist(String name) { this.name = name; }

        public int getSize() { return tracks.size(); }

        public void add(String title, int times) {
            for (int i = 0; i < times; i++) tracks.add(title);
        }

        public void add(String title) { add(title, 1); }   // @JvmOverloads: one overload per default

        public void load(String text) throws IOException {  // @Throws(IOException::class)
            if (text.isBlank()) throw new IOException("nothing to load");
            for (String line : text.split("\n")) add(line.trim());
        }
    }

    static final class Library {                       // @JvmStatic: static members, no INSTANCE needed
        private static final List<Playlist> playlists = new ArrayList<>();
        private Library() {}

        public static void register(Playlist p) { playlists.add(p); }
        public static int getCount() { return playlists.size(); }
    }

    // CHECKS
    public static void main(String[] args) {
        check("Java reads p.name as a public field", "name", () -> Playlist.class.getField("name").getName());
        check("Java can call add with just a title", "add", () -> Playlist.class.getMethod("add", String.class).getName());
        check("Java may catch IOException from load", List.of(IOException.class), () -> List.of(Playlist.class.getMethod("load", String.class).getExceptionTypes()));
        check("register is a static method on Library", true, () -> Modifier.isStatic(Library.class.getMethod("register", Playlist.class).getModifiers()));
        check("adding and loading still work", 4, () -> {
            Playlist p = new Playlist("road trip");
            p.add("Help", 2);
            p.load("Yesterday\nLet It Be");
            return p.getSize();
        });
    }

    interface Check { Object get() throws Exception; }

    static void check(String name, Object expected, Check actual) {
        try {
            Object got = actual.get();
            System.out.println(Objects.equals(expected, got) ? "PASS " + name : "FAIL " + name + ": expected <" + expected + "> but was <" + got + ">");
        } catch (Exception e) {
            System.out.println("FAIL " + name + ": threw " + e);
        }
    }
    // END CHECKS
}
