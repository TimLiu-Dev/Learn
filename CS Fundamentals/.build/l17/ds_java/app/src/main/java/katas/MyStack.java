package katas;

public class MyStack<T> {
    private final MyLinkedList<T> list = new MyLinkedList<>();  // the top is the head

    public void push(T item) { list.addFirst(item); }   // O(1)
    public T pop() { return list.removeFirst(); }       // O(1); throws when empty
    public T peek() { return list.peekFirst(); }        // O(1); null when empty
    public boolean isEmpty() { return list.isEmpty(); }
    public int size() { return list.size(); }
}
