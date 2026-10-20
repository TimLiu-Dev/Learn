import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Delegate {
    // Effective Java Item 18: a reusable forwarding class, every method written by hand
    static class ForwardingSet<E> implements Set<E> {
        private final Set<E> s;
        ForwardingSet(Set<E> s) { this.s = s; }

        public void clear() { s.clear(); }
        public boolean contains(Object o) { return s.contains(o); }
        public boolean isEmpty() { return s.isEmpty(); }
        public int size() { return s.size(); }
        public Iterator<E> iterator() { return s.iterator(); }
        public boolean add(E e) { return s.add(e); }
        public boolean remove(Object o) { return s.remove(o); }
        public boolean containsAll(Collection<?> c) { return s.containsAll(c); }
        public boolean addAll(Collection<? extends E> c) { return s.addAll(c); }
        public boolean removeAll(Collection<?> c) { return s.removeAll(c); }
        public boolean retainAll(Collection<?> c) { return s.retainAll(c); }
        public Object[] toArray() { return s.toArray(); }
        public <T> T[] toArray(T[] a) { return s.toArray(a); }
        @Override public boolean equals(Object o) { return s.equals(o); }
        @Override public int hashCode() { return s.hashCode(); }
        @Override public String toString() { return s.toString(); }
    }

    static class CountingSet<E> extends ForwardingSet<E> {
        private int addCount = 0;
        CountingSet(Set<E> s) { super(s); }

        @Override public boolean add(E e) {
            addCount++;
            return super.add(e);
        }

        @Override public boolean addAll(Collection<? extends E> c) {
            addCount += c.size();
            return super.addAll(c);
        }

        int getAddCount() { return addCount; }
    }

    static class AddOnlyCountingSet<E> extends ForwardingSet<E> {
        private int addCount = 0;
        AddOnlyCountingSet(Set<E> s) { super(s); }

        @Override public boolean add(E e) {
            addCount++;
            return super.add(e);
        }

        int getAddCount() { return addCount; }
    }

    static String describe(Set<String> s) { return "size " + s.size() + ", has Pop: " + s.contains("Pop"); }

    public static void main(String[] args) {
        CountingSet<String> c = new CountingSet<>(new HashSet<>());
        c.addAll(List.of("Snap", "Crackle", "Pop"));
        c.add("Pop");
        System.out.println("CountingSet: counted " + c.getAddCount() + ", " + describe(c));

        AddOnlyCountingSet<String> g = new AddOnlyCountingSet<>(new HashSet<>());
        g.add("Snap");
        g.addAll(List.of("Crackle", "Pop"));
        System.out.println("AddOnlyCountingSet: counted " + g.getAddCount() + ", " + describe(g));
    }
}
