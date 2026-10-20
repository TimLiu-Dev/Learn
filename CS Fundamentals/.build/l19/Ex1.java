public class Ex1 {
    static int bucketIndex(Object key, int capacity) {
        return Math.floorMod(key.hashCode(), capacity);
    }

    public static void main(String[] args) {
        System.out.println("hi by hand: " + (104 * 31 + 105) + ", built-in: " + "hi".hashCode());
        for (String word : new String[] {"hi", "Aa", "BB", "banana", "Kotlin"}) {
            System.out.println(word + " -> hash " + word.hashCode()
                    + " -> bucket " + bucketIndex(word, 16) + " of 16");
        }
        System.out.println("Kotlin in a table of 32: bucket " + bucketIndex("Kotlin", 32));
    }
}
