import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Practice {
    static class Playlist {
        final int maxSize;
        private final List<String> items = new ArrayList<>();

        Playlist(int maxSize) { this.maxSize = maxSize; }

        List<String> songs() {
            return List.copyOf(items);                   // an unmodifiable copy
        }

        void add(String title) {
            if (items.size() >= maxSize) throw new IllegalStateException("playlist is full");
            items.add(title.trim());
        }
    }

    // CHECKS
    public static void main(String[] args) {
        check("add trims and stores songs in order", List.of("Intro", "Outro"), () -> {
            Playlist p = new Playlist(3);
            p.add("  Intro ");
            p.add("Outro");
            return p.songs();
        });
        check("a full playlist refuses more songs", "IllegalStateException [a, b]", () -> {
            Playlist p = new Playlist(2);
            p.add("a");
            p.add("b");
            String thrown = "nothing";
            try { p.add("c"); } catch (RuntimeException e) { thrown = e.getClass().getSimpleName(); }
            return thrown + " " + p.songs();
        });
        check("the backing list is not visible outside", true,
            () -> java.lang.reflect.Modifier.isPrivate(Playlist.class.getDeclaredField("items").getModifiers()));
        check("changing the returned list can't change the playlist", List.of("a"), () -> {
            Playlist p = new Playlist(2);
            p.add("a");
            try { p.songs().addAll(List.of("x", "y", "z")); } catch (UnsupportedOperationException e) { }
            return p.songs();
        });
        check("a list you already got doesn't change later", List.of("a"), () -> {
            Playlist p = new Playlist(3);
            p.add("a");
            List<String> before = p.songs();
            p.add("b");
            return before;
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
