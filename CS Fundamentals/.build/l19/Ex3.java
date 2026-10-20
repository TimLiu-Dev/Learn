import java.util.HashSet;
import java.util.Set;

public class Ex3 {
    static final class Card {                     // final fields = a safe key
        private final int rank;
        private final char suit;
        Card(int rank, char suit) { this.rank = rank; this.suit = suit; }

        @Override public boolean equals(Object o) {
            return o instanceof Card c && c.rank == rank && c.suit == suit;
        }
        @Override public int hashCode() { return 31 * rank + suit; }   // uses the same fields as equals
    }

    public static void main(String[] args) {
        Set<Card> deck = new HashSet<>();
        for (char suit : "SHDC".toCharArray()) for (int rank = 1; rank <= 13; rank++) deck.add(new Card(rank, suit));
        System.out.println("cards: " + deck.size());
        System.out.println("lookup with a new object: " + deck.contains(new Card(12, 'H')));
        Set<Integer> hashes = new HashSet<>();
        for (Card c : deck) hashes.add(c.hashCode());
        System.out.println("distinct hash codes: " + hashes.size());
        System.out.println("new Card(12, 'H').hashCode() = " + new Card(12, 'H').hashCode() + " = 31 * 12 + 'H'");
    }
}
