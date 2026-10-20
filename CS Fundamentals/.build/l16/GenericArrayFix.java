public class GenericArrayFix<T> {
    private final T[] data;

    @SuppressWarnings("unchecked")                    // safe: data never leaves this class
    GenericArrayFix(int capacity) {
        data = (T[]) new Object[capacity];            // an Object[] that we promise to fill with T
    }

    void put(int i, T value) { data[i] = value; }
    T take(int i) { return data[i]; }

    public static void main(String[] args) {
        GenericArrayFix<String> h = new GenericArrayFix<>(4);
        h.put(0, "hi");
        System.out.println(h.take(0).toUpperCase());
    }
}
