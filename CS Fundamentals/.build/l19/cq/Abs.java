public class Abs {
    // "abs makes the hash positive, so % is safe now." Is it?
    static int bucket(String key, int capacity) {
        return Math.abs(key.hashCode()) % capacity;
    }

    public static void main(String[] args) {
        System.out.println(bucket("banana", 10) + " " + bucket("polygenelubricants", 10));
    }
}
