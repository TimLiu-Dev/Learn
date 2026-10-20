package katas;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringJoiner;

public class MyLinkedList<T> implements Iterable<T> {
    private static class Node<T> {                  // static: no hidden outer reference
        final T value;
        Node<T> next;
        Node(T value, Node<T> next) { this.value = value; this.next = next; }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    public void addFirst(T value) {                 // O(1)
        Node<T> n = new Node<>(value, head);
        head = n;
        if (tail == null) tail = n;                 // the only node is also the last
        size++;
    }

    public void addLast(T value) {                  // O(1) thanks to the tail pointer
        Node<T> n = new Node<>(value, null);
        if (tail == null) head = n; else tail.next = n;
        tail = n;
        size++;
    }

    public T removeFirst() {                        // O(1)
        if (head == null) throw new NoSuchElementException("list is empty");
        Node<T> h = head;
        head = h.next;
        if (head == null) tail = null;              // emptied: clear the tail too
        size--;
        return h.value;
    }

    public T peekFirst() { return head == null ? null : head.value; }  // O(1)

    public T get(int index) {                       // O(n)
        Objects.checkIndex(index, size);
        Node<T> cur = head;
        for (int i = 0; i < index; i++) cur = cur.next;
        return cur.value;
    }

    public void reverse() {                         // O(n), no new nodes
        Node<T> prev = null;
        Node<T> curr = head;
        tail = head;                                // the old head becomes the tail
        while (curr != null) {
            Node<T> next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        head = prev;
    }

    @Override public Iterator<T> iterator() {
        return new Iterator<>() {
            private Node<T> cur = head;
            @Override public boolean hasNext() { return cur != null; }
            @Override public T next() {
                if (cur == null) throw new NoSuchElementException();
                T v = cur.value;
                cur = cur.next;
                return v;
            }
        };
    }

    @Override public String toString() {
        StringJoiner sj = new StringJoiner(", ", "[", "]");
        for (T item : this) sj.add(String.valueOf(item));
        return sj.toString();
    }
}
