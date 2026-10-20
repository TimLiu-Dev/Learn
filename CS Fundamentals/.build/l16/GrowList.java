import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

public class GrowList<T> {
    private Object[] data = new Object[2];
    private int size = 0;
    private long copies = 0;                          // instrumentation: elements copied so far

    public int size() { return size; }
    public int capacity() { return data.length; }
    public long copies() { return copies; }

    public void add(T element) {
        if (size == data.length) {                    // full: double the capacity
            data = Arrays.copyOf(data, data.length * 2);   // new array + copy every element
            copies += size;
        }
        data[size++] = element;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        Objects.checkIndex(index, size);              // throws IndexOutOfBoundsException
        return (T) data[index];
    }

    @Override public String toString() {
        StringJoiner sj = new StringJoiner(", ", "[", "]");
        for (int i = 0; i < size; i++) sj.add(String.valueOf(data[i]));
        return sj.toString();
    }

    public static void main(String[] args) {
        GrowList<Integer> list = new GrowList<>();
        int lastCap = list.capacity();
        for (int i = 1; i <= 20; i++) {
            list.add(i * i);
            if (list.capacity() != lastCap) {
                System.out.println("add #" + i + ": capacity " + lastCap + " -> " + list.capacity());
                lastCap = list.capacity();
            }
        }
        System.out.println(list);
        System.out.println("size=" + list.size() + " capacity=" + list.capacity() + " copies=" + list.copies());
        System.out.println(list.get(19));
        System.out.println(list.get(20));
    }
}
