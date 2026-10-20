public class ObjectArrayHeap {
    static class Heap<T extends Comparable<T>> {
        @SuppressWarnings("unchecked")
        private T[] a = (T[]) new Object[4];       // compiles... T erases to Comparable
        void add(T x) { a[0] = x; }
    }

    public static void main(String[] args) {
        new Heap<Integer>().add(1);
    }
}
