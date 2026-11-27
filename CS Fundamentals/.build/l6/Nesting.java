import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Nesting {
    public static void main(String[] args) {
        Playlist.Song intro = new Playlist.Song("Intro", 90);  // no Playlist needed
        Playlist mix = new Playlist("Mix");
        mix.add(intro.title);
        mix.add("Outro");
        Playlist.Cursor cursor = mix.new Cursor();              // needs a Playlist to belong to
        System.out.println(cursor.next() + " " + cursor.next() + " " + cursor.next() + " (from " + cursor.owner() + ")");

        // A peek at the compiled fields: which class holds a hidden Playlist?
        System.out.println("Song:   " + names(Playlist.Song.class));
        System.out.println("Cursor: " + names(Playlist.Cursor.class));
    }

    static List<String> names(Class<?> c) {
        return Arrays.stream(c.getDeclaredFields()).map(f -> f.getName()).sorted().toList();
    }
}

class Playlist {
    final String name;
    private final List<String> songs = new ArrayList<>();
    Playlist(String name) { this.name = name; }
    void add(String song) { songs.add(song); }

    // static: no link to any Playlist (Kotlin's default)
    static class Song {
        final String title;
        final int seconds;
        Song(String title, int seconds) { this.title = title; this.seconds = seconds; }
    }

    // no modifier: an inner class (Java's default), tied to one Playlist
    class Cursor {
        private int index = 0;
        String next() { return index < songs.size() ? songs.get(index++) : null; }
        String owner() { return Playlist.this.name; }   // the outer instance, by name
    }
}
