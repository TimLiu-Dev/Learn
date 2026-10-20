import java.util.Arrays;

public class Resize {
    public static void main(String[] args) {
        String[] data = new String[2];             // capacity 2
        int size = 0;
        data[size++] = "a";
        data[size++] = "b";                        // full: size == capacity
        String[] bigger = new String[data.length * 2];
        System.arraycopy(data, 0, bigger, 0, size);    // copy every element: O(n)
        data = bigger;                             // re-point; the old array is garbage now
        data[size++] = "c";
        System.out.println("size=" + size + " capacity=" + data.length + " " + Arrays.toString(data));
    }
}
