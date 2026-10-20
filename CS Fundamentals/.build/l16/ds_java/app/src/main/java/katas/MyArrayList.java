package katas;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringJoiner;

public class MyArrayList<T> implements Iterable<T> {
    private T[] data;
    private int size = 0;

    public MyArrayList() { this(4); }

    @SuppressWarnings("unchecked")                         // safe: data never escapes this class
    public MyArrayList(int initialCapacity) {
        if (initialCapacity < 1)
            throw new IllegalArgumentException("initialCapacity must be at least 1, was " + initialCapacity);
        data = (T[]) new Object[initialCapacity];
    }

    public int size() { return size; }
    public int capacity() { return data.length; }
    public boolean isEmpty() { return size == 0; }

    public void add(T element) {                           // amortised O(1)
        if (size == data.length) grow();
        data[size++] = element;
    }

    public void add(int index, T element) {                // O(n): shifts the tail right
        Objects.checkIndex(index, size + 1);               // index == size is allowed (append)
        if (size == data.length) grow();
        System.arraycopy(data, index, data, index + 1, size - index);
        data[index] = element;
        size++;
    }

    public T get(int index) {                              // O(1)
        Objects.checkIndex(index, size);
        return data[index];
    }

    public T set(int index, T element) {                   // O(1), returns the old value
        T old = get(index);
        data[index] = element;
        return old;
    }

    public T removeAt(int index) {                         // O(n): shifts the tail left
        T old = get(index);
        System.arraycopy(data, index + 1, data, index, size - index - 1);
        data[--size] = null;                               // drop the stale reference (EJ Item 7)
        return old;
    }

    private void grow() {
        data = Arrays.copyOf(data, data.length * 2);       // new array, old elements copied: O(n)
    }

    @Override public Iterator<T> iterator() {
        return new Iterator<>() {
            private int next = 0;
            @Override public boolean hasNext() { return next < size; }
            @Override public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                return get(next++);
            }
        };
    }

    @Override public String toString() {
        StringJoiner sj = new StringJoiner(", ", "[", "]");
        for (T t : this) sj.add(String.valueOf(t));
        return sj.toString();
    }
}
