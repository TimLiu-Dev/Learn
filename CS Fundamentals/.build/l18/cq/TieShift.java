import java.util.*;

public class TieShift {
    record Card(int rank, char suit) {
        public String toString() { return "" + rank + suit; }
    }

    // Insertion sort by rank, but the loop shifts while a[j] >= key.
    static void sort(List<Card> a) {
        for (int i = 1; i < a.size(); i++) {
            Card key = a.get(i);
            int j = i - 1;
            while (j >= 0 && a.get(j).rank() >= key.rank()) {
                a.set(j + 1, a.get(j));
                j--;
            }
            a.set(j + 1, key);
        }
    }

    public static void main(String[] args) {
        List<Card> hand = new ArrayList<>(List.of(
            new Card(5, 'h'), new Card(2, 's'), new Card(5, 'c'), new Card(2, 'd')));
        sort(hand);
        System.out.println(hand);
    }
}
