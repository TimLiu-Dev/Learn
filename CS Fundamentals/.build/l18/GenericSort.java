import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GenericSort {
    // Natural order: T must know how to compare itself (L13's upper bound).
    static <T extends Comparable<? super T>> void insertionSort(List<T> a) {
        insertionSort(a, Comparator.naturalOrder());
    }

    // Any order: the caller passes the rule as a Comparator (L14's function-as-value).
    static <T> void insertionSort(List<T> a, Comparator<? super T> cmp) {
        for (int i = 1; i < a.size(); i++) {
            T key = a.get(i);
            int j = i - 1;
            while (j >= 0 && cmp.compare(key, a.get(j)) < 0) {
                a.set(j + 1, a.get(j));
                j--;
            }
            a.set(j + 1, key);
        }
    }

    record Song(String title, int seconds) {}

    public static void main(String[] args) {
        List<String> words = new ArrayList<>(List.of("pear", "fig", "kiwi", "apple", "date"));
        insertionSort(words);
        System.out.println(words);
        insertionSort(words, Comparator.comparing(String::length)
                .thenComparing(Comparator.<String>reverseOrder()));
        System.out.println(words);

        List<Song> songs = new ArrayList<>(List.of(new Song("Hey", 210), new Song("Run", 185), new Song("Go", 210)));
        insertionSort(songs, Comparator.comparingInt(Song::seconds));
        System.out.println(songs.stream().map(Song::title).toList());
    }
}
