package katas;

public class MyQueue<T> {
    private T[] items;
    private int head = 0;                           // index of the front element
    private int tail = 0;                           // index of the next free slot
    private int size = 0;

    public MyQueue() { this(4); }

    @SuppressWarnings("unchecked")                  // safe: only Ts are ever stored (L13)
    public MyQueue(int initialCapacity) {
        if (initialCapacity <= 0) throw new IllegalArgumentException("capacity must be positive");
        items = (T[]) new Object[initialCapacity];
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    public void offer(T item) {                     // amortised O(1)
        if (size == items.length) grow();
        items[tail] = item;
        tail = (tail + 1) % items.length;
        size++;
    }

    public T poll() {                               // O(1); null when empty
        if (size == 0) return null;
        T item = items[head];
        items[head] = null;
        head = (head + 1) % items.length;
        size--;
        return item;
    }

    public T peek() { return size == 0 ? null : items[head]; }

    @SuppressWarnings("unchecked")
    private void grow() {                           // O(n), only when full
        T[] bigger = (T[]) new Object[items.length * 2];
        for (int i = 0; i < size; i++) bigger[i] = items[(head + i) % items.length];
        items = bigger;
        head = 0;
        tail = size;
    }
}
