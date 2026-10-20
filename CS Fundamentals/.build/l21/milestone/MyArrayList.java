import java.util.Arrays;

// Minimal stand-in for your L16 MyArrayList. Swap in your own.
public class MyArrayList<T> {
    private Object[] data = new Object[8];
    private int size = 0;

    public int size() { return size; }

    public void add(T x) {
        if (size == data.length) data = Arrays.copyOf(data, size * 2);   // double: amortised O(1)
        data[size++] = x;
    }

    @SuppressWarnings("unchecked")
    public T get(int i) {
        if (i < 0 || i >= size) throw new IndexOutOfBoundsException("index " + i + ", size " + size);
        return (T) data[i];
    }
}
